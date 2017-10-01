package com.cxf.netty.client;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cxf.netty.client.codec.TcpEncode;
import com.cxf.netty.client.util.Msg;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TcpClientInboundHandler extends ChannelInboundHandlerAdapter {

	private static String encoding = "utf8";
	private static Logger logger = LoggerFactory.getLogger(TcpEncode.class);

	int times = 0;

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		byte cmd = (byte) 0x01;
		logger.debug("cmd:" + Byte.toString(cmd));
		try {
			ctx.writeAndFlush(Msg.intMsg(cmd, UUID.randomUUID().toString().getBytes(encoding)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		byte cmd = (byte) 0xBE;
		// 处理数据
		byte[] data;
		try {
			data = Msg.conventMsg(msg);
			Thread.sleep(1000 * 1);
			if (times < 0) {
				times++;
				ctx.writeAndFlush(Msg.intMsg(cmd, UUID.randomUUID().toString().getBytes(encoding)));
			}

			System.out.println(new String(data));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

		logger.error("caught an ex, channel={}, exception={}", ctx.channel(), cause);
		ctx.close();
	}

}

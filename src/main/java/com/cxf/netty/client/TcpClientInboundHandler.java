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

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		byte cmd = 0x01;

		try {
			ctx.writeAndFlush(Msg.intMsg(cmd, UUID.randomUUID().toString().getBytes(encoding)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		byte cmd = 0x01;
		// 处理数据
		byte[] data;
		try {
			data = Msg.conventMsg(msg);
			Thread.sleep(1000 * 60);
			ctx.writeAndFlush(Msg.intMsg(cmd, UUID.randomUUID().toString().getBytes(encoding)));
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

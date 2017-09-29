package com.cxf.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cxf.netty.client.codec.TcpEncode;
import com.cxf.netty.client.util.Msg;

public class TcpRcvClientInboundHandler extends ChannelInboundHandlerAdapter {

    private static String encoding = "utf8";
    private static Logger logger = LoggerFactory.getLogger(TcpEncode.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        byte cmd = 0x03;

        try {
            ctx.writeAndFlush(Msg.intMsg(cmd, "e7319fee_1000".getBytes(encoding)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte cmd = 0x05;
        // 处理数据
        byte[] data;
        try {
            data = Msg.conventMsg(msg);
            logger.debug("rcv msg:" + new String(data));
            Thread.sleep(1000 * 10);
            ctx.writeAndFlush(Msg.intMsg(cmd, "".getBytes(encoding)));
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

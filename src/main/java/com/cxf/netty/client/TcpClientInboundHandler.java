package com.cxf.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cxf.netty.client.codec.TcpEncode;
import com.cxf.netty.client.util.Msg;

public class TcpClientInboundHandler extends ChannelInboundHandlerAdapter {

    private static String encoding = "utf8";
    private static Logger logger = LoggerFactory.getLogger(TcpEncode.class);

    int times = 0;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        byte cmd = (byte) 0x01;
        logger.debug("cmd:" + Byte.toString(cmd));
        try {
            ctx.writeAndFlush(Msg.intMsg(cmd, "123456".getBytes(encoding)));
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
            Thread.sleep(1000 * 10);
            if (times < 10) {
                times++;
                ctx.writeAndFlush(Msg.intMsgLls(cmd, times));
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

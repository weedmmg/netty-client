package com.cxf.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import com.cxf.netty.client.codec.TcpDecode;
import com.cxf.netty.client.codec.TcpEncode;

public class TcpClient implements Runnable {

    public void run() {
        try {
            // local
            // this.connect("192.168.1.19", 8887);

            // nginx
            // this.connect("192.168.1.106", 8887);

            // server
            // this.connect("116.62.17.108", 8887);
            // this.connect("testa.keyuebao.net", 8887);

            // this.connect("testb.keyuebao.net", 8886);

            // this.connect("testc.keyuebao.net", 8886);

            this.connect("lhtest.keyuebao.net", 8887);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void connect(String host, int port) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new TcpDecode());
                    ch.pipeline().addLast(new TcpEncode());
                    ch.pipeline().addLast(new TcpClientInboundHandler());
                }
            });
            ChannelFuture f = b.connect(host, port).sync();

            f.channel().closeFuture().sync();
            // }

        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int threadNum = 100;
        Thread[] threads = new Thread[threadNum];
        TcpClient client = new TcpClient();

        for (int i = 0; i < threadNum; i++)
            threads[i] = new Thread(client, "thread-" + i);
        long begin = System.currentTimeMillis();
        for (Thread t : threads)
            t.start();

    }
}

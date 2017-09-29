package com.cxf.netty.client;

import com.cxf.netty.client.codec.TcpDecode;
import com.cxf.netty.client.codec.TcpEncode;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TcpClient {
	public void connect(String host, int port) throws Exception {
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
		TcpClient client = new TcpClient();
		for (int k = 0; k < 1; k++) {
			client.connect("192.168.1.21", 8887);

			System.out.println(k);
		}
	}
}

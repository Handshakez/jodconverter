package com.mydocket.pdfmaker;

import com.mydocket.pdfmaker.codec.CommandDecoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
	
	private int port;
	
	public boolean running = false;
	
	public Server(int port) {
		this.port = port;
	}
	
	public void run() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap strap = new ServerBootstrap();
			strap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
//						ch.pipeline().addLast(new ChannelHandler());
						ch.pipeline().addLast(new CommandDecoder(), new CommandHandler());
					}
				})
				.option(ChannelOption.SO_BACKLOG, 128)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			ChannelFuture future = strap.bind(port).sync();
			this.running = true;
			future.channel().closeFuture().sync();
			
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
			this.running = false;
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		System.out.println("Hello world");
		Server s = new Server(7001);
		s.run();
	}
}

package com.mydocket.pdfserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.artofsolving.jodconverter.office.OfficeManager;

import com.mydocket.pdfmaker.codec.CommandDecoder;
import com.mydocket.pdfmaker.codec.ConversionResultEncoder;
import com.mydocket.pdfmaker.codec.ErrorResultEncoder;
import com.mydocket.pdfmaker.codec.FileResultEncoder;

public class Server {
	
	private int port;
	private OfficeManager officeManager;
	
	public boolean running = false;
	
	public Server(int port, OfficeManager officeConfig) {
		this.port = port;
		this.officeManager = officeConfig;
		
//		pipeline = new ChannelHandler[] {
//			new CommandDecoder(),
//			new CommandHandler(officeConfig),
//		};
		
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
						ch.pipeline().addLast(
								// en/decoders have to be added before handler (??)
								new CommandDecoder(), 
								new ConversionResultEncoder(),
//								new FileResultEncoder(),
//								new ErrorResultEncoder(),
								new CommandHandler(Server.this.officeManager)
								);
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
		Server s = new Server(7001, null);
		s.run();
	}
}

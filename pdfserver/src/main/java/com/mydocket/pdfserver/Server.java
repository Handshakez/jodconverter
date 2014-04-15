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

import com.mydocket.pdfserver.codec.CommandDecoder;
import com.mydocket.pdfserver.codec.ConversionResultEncoder;

public class Server {
	
	private int port;
	private OfficeManager officeManager;
	
	// http://stackoverflow.com/questions/11288957/how-to-stop-netty-from-listening-and-accepting-on-server-socket
	//private ServerSocketChannel ssc = null;
	// No longer seems to be the case?
	
	private EventLoopGroup elgBosses;
	private EventLoopGroup elgWorkers;
	
	
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
		this.elgBosses = new NioEventLoopGroup();
		this.elgWorkers = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(elgBosses, elgWorkers)
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
			
			ChannelFuture future = bootstrap.bind(port).sync();
			this.running = true;
			future.channel().closeFuture().sync();
			
		} finally {
			this.stop();
		}
	}
	
	public synchronized void stop() {
		if (this.running) {
			this.elgWorkers.shutdownGracefully();
			this.elgBosses.shutdownGracefully();
			this.running = false;
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		System.out.println("Hello world");
		Server s = new Server(7001, null);
		s.run();
	}
}

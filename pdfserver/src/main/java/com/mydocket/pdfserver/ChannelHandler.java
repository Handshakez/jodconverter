package com.mydocket.pdfserver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ChannelHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		//ByteBuf in = (ByteBuf) msg;
		
		ctx.write(msg);
		ctx.flush();
//		ByteBuf in = (ByteBuf) msg;
//		try {
//			while (in.isReadable()) {
//				System.out.println((char) in.readByte());
//				System.out.flush();
//			}
//		} finally {
//			ReferenceCountUtil.release(msg);
//		}
		//((ByteBuf) msg).release();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}

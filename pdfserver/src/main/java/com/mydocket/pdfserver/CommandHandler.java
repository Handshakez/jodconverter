package com.mydocket.pdfserver;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.artofsolving.jodconverter.office.OfficeManager;

import com.mydocket.pdfmaker.command.HelloCommand;
import com.mydocket.pdfmaker.command.ICommand;
import com.mydocket.pdfmaker.command.PushCommand;
import com.mydocket.pdfmaker.command.StopCommand;
import com.mydocket.pdfmaker.converter.CommandAdapter;
import com.mydocket.pdfmaker.converter.ConversionResult;

public class CommandHandler extends ChannelInboundHandlerAdapter {
	private CommandAdapter adapter = null;
	
	public CommandHandler(OfficeManager officeManager) {
		this.adapter = new CommandAdapter(officeManager);
	}
	
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ICommand cmd = (ICommand) msg;
		
		if (cmd instanceof HelloCommand) {
			// this doesn't work right, and will hang.  Encoder needed?
			// ctx.write("Hi.");
			this.println(ctx, "Hi.");
		} else if (cmd instanceof StopCommand) {
			ChannelFuture f = this.println(ctx, "Goodbye.");
			ctx.close();
			f.addListener(ChannelFutureListener.CLOSE);
		} else if (cmd instanceof PushCommand) {
			ConversionResult result = adapter.consume((PushCommand) cmd);
		    //this.println(ctx, "Push: Was Error? " + result.isError());
		    ChannelFuture f = ctx.writeAndFlush(result);
		    f.addListener(ChannelFutureListener.CLOSE);
			//this.println(ctx, "Push.");
			//this.println(ctx, "Size2: " + ((PushCommand) cmd).getContent());
		}
		//((ByteBuf) msg).release();
	}

	private ChannelFuture println(ChannelHandlerContext ctx, String msg) {
		ByteBuf buf = ctx.alloc().buffer(msg.length() + 1);
		buf.writeBytes(msg.getBytes());
		buf.writeByte(13);
		ChannelFuture f = ctx.writeAndFlush(buf);
		return f;
	}
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

}

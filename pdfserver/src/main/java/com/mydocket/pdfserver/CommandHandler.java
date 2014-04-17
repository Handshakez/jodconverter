package com.mydocket.pdfserver;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;

import org.artofsolving.jodconverter.office.OfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mydocket.pdfserver.command.HelloCommand;
import com.mydocket.pdfserver.command.ICommand;
import com.mydocket.pdfserver.command.PushCommand;
import com.mydocket.pdfserver.command.StopCommand;
import com.mydocket.pdfserver.converter.FileConverter;
import com.mydocket.pdfserver.converter.Observer;

public class CommandHandler extends ChannelInboundHandlerAdapter {
	private FileConverter converter = null;
	
	private static final Logger logger = LoggerFactory
			.getLogger(CommandHandler.class);
	
	public CommandHandler(OfficeManager officeManager) {
		this.converter = new FileConverter(officeManager);
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
			PushCommand push = (PushCommand) cmd;
			converter.convert(ctx, push.getContent());
		    //this.println(ctx, "Push: Was Error? " + result.isError());
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
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		//cause.printStackTrace();
		if (cause instanceof ReadTimeoutException) {
			Observer obs = new Observer(ctx);
			obs.observe(cause);
			obs.finish();
		} else {
			try {
				super.exceptionCaught(ctx, cause);
			} catch (Exception e) {
				logger.error("Error in command handler", e);
			}
			ctx.close();
		}
	}

}

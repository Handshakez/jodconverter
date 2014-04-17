package com.mydocket.pdfserver.converter;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;

import com.mydocket.pdfserver.converter.results.ErrorResult;
import com.mydocket.pdfserver.converter.results.FileResult;
import com.mydocket.pdfserver.converter.results.IConversionResult;

public class Observer {

	private ChannelHandlerContext ctx;
	private ChannelFuture future = null;
	
	public Observer(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}
	
	private void observe(IConversionResult  result) {
		future = ctx.writeAndFlush(result);
	}
	
	public void observe(File result) {
		this.observe(new FileResult(result));
	}

	public void observe(Throwable result) {
		this.observe(new ErrorResult(result));
	}
	
	public void finish() {
		if (future != null) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}
}

package com.mydocket.pdfmaker.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;

import com.mydocket.pdfmaker.converter.results.*;

public class ConversionResultEncoder extends
		MessageToByteEncoder<IConversionResult> {

	@Override
	protected void encode(ChannelHandlerContext ctx, IConversionResult result,
			ByteBuf buf) throws Exception {
		
		if (result instanceof ErrorResult) {
			outputError(ctx, (ErrorResult) result, buf);
		} else if (result instanceof FileResult) {
			outputSuccess(ctx, (FileResult) result, buf);
		}
	}
	
	@Override
	public boolean acceptOutboundMessage(Object msg) throws Exception {
        return msg != null && msg instanceof IConversionResult;
    }
	
	
	private void writeLine(ByteBuf buf, String msg) {
		buf.writeBytes(msg.getBytes());
		buf.writeByte(13);
	}
	
	private void outputError(ChannelHandlerContext ctx, ErrorResult result,
			ByteBuf buf) throws Exception {
		writeLine(buf, "1");
		buf.writeBytes(result.getMessage().getBytes());
	}

	private void writeFile(ByteBuf buf, File file) throws Exception {
		long length  = file.length();
		
		writeLine(buf,length + "");
		FileInputStream fis = null;
		try {
			int chunk   = 2048 * 10;	// 20kbytes okay?
			long total  = 0;
			fis = new FileInputStream(file);
			while (total < length) {
				total += buf.writeBytes(fis, chunk);
			}
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}
	
	private void outputSuccess(ChannelHandlerContext ctx, FileResult result,
			ByteBuf buf) throws Exception {
		writeLine(buf, "0");
		writeFile(buf, result.getFile());
	}
}

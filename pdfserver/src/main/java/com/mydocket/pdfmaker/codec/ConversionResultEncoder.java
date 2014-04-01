package com.mydocket.pdfmaker.codec;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;

import com.mydocket.pdfmaker.converter.ConversionResult;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ConversionResultEncoder extends
		MessageToByteEncoder<ConversionResult> {

	@Override
	protected void encode(ChannelHandlerContext ctx, ConversionResult result,
			ByteBuf buf) throws Exception {
		
		if (result.isError()) {
			outputError(ctx, result, buf);
		} else {
			outputSuccess(ctx, result, buf);
		}
	}
	
	private void writeLine(ByteBuf buf, String msg) {
		buf.writeBytes(msg.getBytes());
		buf.writeByte(13);
	}
	
	private void outputError(ChannelHandlerContext ctx, ConversionResult result,
			ByteBuf buf) throws Exception {
		writeLine(buf, "1");
		buf.writeBytes(result.getErrorMessage().getBytes());
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
	
	private void outputSuccess(ChannelHandlerContext ctx, ConversionResult result,
			ByteBuf buf) throws Exception {
		writeLine(buf, "0");
		writeFile(buf, result.getThumbFile());
		writeFile(buf, result.getFullFile());
	}
}

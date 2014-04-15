package com.mydocket.pdfserver.codec;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;

import com.mydocket.pdfserver.converter.results.FileResult;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class FileResultEncoder extends MessageToByteEncoder<FileResult> {

	@Override
	protected void encode(ChannelHandlerContext ctx, FileResult msg,
			ByteBuf buf) throws Exception {
		WriteLine.writeLine(buf, "0");
		writeFile(buf, msg.getFile());
	}
	
	private void writeFile(ByteBuf buf, File file) throws Exception {
		long length  = file.length();
		
		WriteLine.writeLine(buf,length + "");
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
	

}

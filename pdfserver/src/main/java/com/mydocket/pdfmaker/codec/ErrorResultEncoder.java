package com.mydocket.pdfmaker.codec;

import com.mydocket.pdfmaker.converter.results.ErrorResult;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ErrorResultEncoder extends MessageToByteEncoder<ErrorResult> {

	@Override
	protected void encode(ChannelHandlerContext ctx, ErrorResult msg,
			ByteBuf out) throws Exception {
		WriteLine.writeLine(out, "1");
		out.writeBytes(msg.getMessage().getBytes());
	}

}

package com.mydocket.pdfmaker.codec;

import io.netty.buffer.ByteBuf;

public class WriteLine {

	public static void writeLine(ByteBuf buf, String msg) {
		buf.writeBytes(msg.getBytes());
		buf.writeByte(13);
	}
	
}

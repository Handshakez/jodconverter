package com.mydocket.pdfmaker.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

import com.mydocket.pdfmaker.command.ErrorCommand;
import com.mydocket.pdfmaker.command.HelloCommand;
import com.mydocket.pdfmaker.command.ICommand;
import com.mydocket.pdfmaker.command.PushCommand;
import com.mydocket.pdfmaker.command.StopCommand;

public class CommandDecoder extends ReplayingDecoder<DecoderState> {

	private PushCommand command = null;
	private StopCommand stop    = new StopCommand();
	private HelloCommand hello  = new HelloCommand();
	byte newline = 13;
	
	public CommandDecoder() {
		super(DecoderState.READ_COMMAND);
	}
	
	@Override
	protected void decode (ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		switch (state()) {
		case READ_COMMAND:
			String cmd = this.readLine(in);
			// TODO: is it really necessary to check for null each time?
			if (cmd == null) {
				return;
			} else if (cmd.equals(stop.getName())) {
				complete(out, stop);
				return;
			} else if (cmd.equals(hello.getName())) {
				complete(out, hello);
				return;
			} else {
				this.command = new PushCommand();
				checkpoint(DecoderState.READ_FILENAME);
			}
			break;
		case READ_FILENAME:
			String fname = this.readLine(in);
			if (fname == null) {
				return;
			}
			this.command.setFileName(fname);
			checkpoint(DecoderState.READ_LENGTH);
			break;
		case READ_LENGTH:
			String len = this.readLine(in);
			if (len == null) {
				return;
			}
			try {
				// TODO: easier to just read the next integer?
				// No, because it's a string, not an integer being passed
				int length = Integer.parseInt(len);
				this.command.setLength(length);
			} catch (NumberFormatException nfe) {
				// if this happens, you're hosed.
				complete(out, new ErrorCommand("Illegal length value: " + len));
				return;
			}
			checkpoint(DecoderState.READ_CONTENT);
			break;
		case READ_CONTENT:
			// TODO: is this really handling chunking correctly?
			ByteBuf content = in.alloc().directBuffer(this.command.getLength());
			content.writeBytes(in, this.command.getLength());
			this.command.setContent(content);
			complete(out, command);
//			if (in.readableBytes() >= this.command.getLength()) {
//				ByteBuf content = in.alloc().directBuffer(this.command.getLength());
//				content.writeBytes(in, this.command.getLength());
//				complete(out, command);
//			}
			break;
		}
	}
	
	private void complete(List<Object> out, ICommand cmd) {
		out.add(cmd);
		checkpoint(DecoderState.READ_COMMAND);
	}
	
	private String readLine(ByteBuf in) throws Exception {
		int size = in.bytesBefore(newline);
		if (size == -1) {
			return null;
		}
		StringBuilder sb = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
			// do these need to be two bytes chars?  Because python is stupid
			sb.append((char) in.readByte());
		}
		// read newline as well.
		in.skipBytes(1);
		return sb.toString();
	}
	
}

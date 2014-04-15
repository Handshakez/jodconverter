package com.mydocket.pdfserver.command;

import java.io.File;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.multipart.DiskFileUpload;

public class PushCommand implements ICommand {

	private String fileName;
	private int length;
	private File content;
	
	
	@Override
	public String getName() {
		return "PUSH";
	}

	public String getFileName() {
		return this.fileName;
	}
	
	public void setFileName(String fname) {
		this.fileName = fname;
	}

	public int getLength() {
		return this.length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
	public File getContent() {
		return this.content;
	}
	
	public void setContent(ByteBuf buffer) throws Exception {
		// surprisingly, there is nothing in the
		// code base that automatically turns a ByteBuffer into a file.
		// This is lifted from the http file upload stuff
		DiskFileUpload upload = new DiskFileUpload(
				this.fileName,	// name 
				this.fileName,	// file name
				"",				// content type
				"",				// transfer encoding
				null,			// charset
				this.length
				);
		 
		// TODO: is the buffer actually full at this point?
		upload.setContent(buffer);
		this.content = upload.getFile();
	}

}

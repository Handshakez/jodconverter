package com.mydocket.pdfserver.converter.results;

import java.io.File;

public class FileResult implements IConversionResult  {
	private File file;
	
	public FileResult(File file) {
		this.file = file;
	}
	
	public File getFile() {
		return this.file;
	}
}

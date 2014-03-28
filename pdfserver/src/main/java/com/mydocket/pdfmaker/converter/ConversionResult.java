package com.mydocket.pdfmaker.converter;

import java.io.File;

public class ConversionResult {

	File thumbFile;
	File fullFile;
	
	String errorMessage = null;;
	
	
	public File getThumbFile() {
		return this.thumbFile;
	}
	
	public File getFullFile() {
		return this.fullFile;
	}
	
	public String getErrorMessage() {
		return this.errorMessage;
	}
	
	public boolean isError() {
		return this.errorMessage != null;
	}
	
}

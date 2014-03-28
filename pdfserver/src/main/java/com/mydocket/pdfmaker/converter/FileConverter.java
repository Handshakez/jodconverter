package com.mydocket.pdfmaker.converter;

import java.io.File;

public class FileConverter {

	
	public ConversionResult convert(File input) {
		ConversionResult result = new ConversionResult();
		
		
		try {
			// create open office file
			Object ood = importFile(input);
			result.thumbFile = createThumb(ood);
			result.fullFile  = createFull(ood);
		} catch (Exception e) {
			// TODO: logger
			result.errorMessage = e.getMessage();
		}
		return result;
	}

	
	private Object importFile(File input) {
		return null;
	}
	
	private File createThumb(Object ood) {
		return null;
	}
	
	private File createFull(Object ood) {
		return null;
	}
	
}

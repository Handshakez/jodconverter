package com.mydocket.pdfmaker.converter;

import com.mydocket.pdfmaker.command.PushCommand;

public class CommandAdapter {
	
	
	public ConversionResult consume(PushCommand command) {
		// TODO: singleton?
		// TODO: should this be here, or elsewhere?
		FileConverter converter = new FileConverter();
		ConversionResult result = converter.convert(command.getContent());
		return result;
	}
	
	
	

}

package com.mydocket.pdfmaker.converter;

import org.artofsolving.jodconverter.office.OfficeManager;

import com.mydocket.pdfmaker.command.PushCommand;

public class CommandAdapter {
	
	private FileConverter converter = null;
	
	public CommandAdapter(OfficeManager officeManager) {
		this.converter = new FileConverter(officeManager);
	}
	
	
	public ConversionResult consume(PushCommand command) {
		// TODO: singleton?
		// TODO: should this be here, or elsewhere?
		ConversionResult result = this.converter.convert(command.getContent());
		return result;
	}
	
	
	

}

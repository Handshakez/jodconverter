package com.mydocket.pdfmaker.command;

public class HelloCommand implements ICommand {

	@Override
	public String getName() {
		return "HELO";
	}

}

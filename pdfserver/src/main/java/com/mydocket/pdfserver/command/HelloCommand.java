package com.mydocket.pdfserver.command;

public class HelloCommand implements ICommand {

	@Override
	public String getName() {
		return "HELO";
	}

}

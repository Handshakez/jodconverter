package com.mydocket.pdfserver.command;


public class StopCommand implements ICommand {

	@Override
	public String getName() {
		return "STOP";
	}
}

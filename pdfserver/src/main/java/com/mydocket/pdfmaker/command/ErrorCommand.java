package com.mydocket.pdfmaker.command;

public class ErrorCommand implements ICommand {

	private String msg;
	
	public ErrorCommand(String msg) {
		this.msg = msg;
	}
	
	@Override
	public String getName() {
		return "ERRO";
	}

}

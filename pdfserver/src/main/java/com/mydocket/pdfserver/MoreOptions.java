package com.mydocket.pdfserver;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Options;

public class MoreOptions extends Options {

	private List<String> required = null;

	public MoreOptions() {
		super();
		this.required = new ArrayList<String>();
	}
	
	// allows us to check after our --help checks
	public void addRequired(String name, String msg) {
		this.addOption(name, true, msg);
		this.required.add(name);
	}

	public List<String> getRequired() {
		return this.required;
	}
	
}

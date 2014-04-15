package com.mydocket.pdfserver.converter.results;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ErrorResult implements IConversionResult {
	private static final Logger logger = LoggerFactory
	.getLogger(ErrorResult.class);

	private String errorMessage = null;


	public ErrorResult(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		errorMessage = sw.toString(); 
		logger.warn("Error running open office", e);
	}

	public String getMessage() {
		return this.errorMessage;
	}
}

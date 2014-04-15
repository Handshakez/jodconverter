package com.mydocket.pdfserver.converter;

import java.util.Collections;

import org.artofsolving.jodconverter.document.DocumentFamily;
import org.artofsolving.jodconverter.document.DocumentFormat;

/**
 * Exports all of the pages into a PDF.
 * 
 * @author aarvesen
 *
 */
public class AllPagesFormat extends DocumentFormat {
	private static AllPagesFormat instance;
	
	// package access for testing
	AllPagesFormat(){
		super("Portable Document Format", "pdf", "application/pdf");
		setStoreProperties(DocumentFamily.TEXT, Collections.singletonMap("FilterName", "writer_pdf_Export"));
		setStoreProperties(DocumentFamily.SPREADSHEET, Collections.singletonMap("FilterName", "calc_pdf_Export"));
		setStoreProperties(DocumentFamily.PRESENTATION, Collections.singletonMap("FilterName", "impress_pdf_Export"));
		setStoreProperties(DocumentFamily.DRAWING, Collections.singletonMap("FilterName", "draw_pdf_Export"));
//		setStoreProperties(DocumentFamily.TEXT, pdfExport("writer_pdf_Export"));
//		setStoreProperties(DocumentFamily.SPREADSHEET, pdfExport("calc_pdf_Export"));
//		setStoreProperties(DocumentFamily.PRESENTATION, pdfExport("impress_pdf_Export"));
//		setStoreProperties(DocumentFamily.DRAWING, pdfExport("draw_pdf_Export"));
	}
	
	public static synchronized AllPagesFormat getInstance() {
		if (instance == null) {
			instance = new AllPagesFormat();
		}
		return instance;
	}

}

package com.mydocket.pdfserver.converter;

import java.util.HashMap;
import java.util.Map;

import org.artofsolving.jodconverter.document.DocumentFamily;
import org.artofsolving.jodconverter.document.DocumentFormat;

/**
 * Produces a single page of pdf output.
 * 
 * @author aarvesen
 *
 */
public class SinglePageFormat extends DocumentFormat {

	private static SinglePageFormat instance;
	
	// package access for testing
	SinglePageFormat(){
		super("Portable Document Format", "pdf", "application/pdf");
//		pdf.setStoreProperties(DocumentFamily.TEXT, Collections.singletonMap("FilterName", "writer_pdf_Export"));
//		pdf.setStoreProperties(DocumentFamily.SPREADSHEET, Collections.singletonMap("FilterName", "calc_pdf_Export"));
//		pdf.setStoreProperties(DocumentFamily.PRESENTATION, Collections.singletonMap("FilterName", "impress_pdf_Export"));
//		pdf.setStoreProperties(DocumentFamily.DRAWING, Collections.singletonMap("FilterName", "draw_pdf_Export"));
		setStoreProperties(DocumentFamily.TEXT, pdfExport("writer_pdf_Export"));
		setStoreProperties(DocumentFamily.SPREADSHEET, pdfExport("calc_pdf_Export"));
		setStoreProperties(DocumentFamily.PRESENTATION, pdfExport("impress_pdf_Export"));
		setStoreProperties(DocumentFamily.DRAWING, pdfExport("draw_pdf_Export"));
	}
	
	public static synchronized SinglePageFormat getInstance() {
		if (instance == null) {
			instance = new SinglePageFormat();
		}
		return instance;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map<String, ?> pdfExport(String filterName) {
		Map map  = new HashMap();
		Map data = new HashMap();

		data.put("PageRange", "1");
		
		map.put("FilterName", filterName);
		map.put("FilterData", data);
		return map;
	}
}

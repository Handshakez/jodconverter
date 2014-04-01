package com.mydocket.pdfmaker.converter;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileConverter {
	
	
	private static final Logger logger = LoggerFactory
			.getLogger(FileConverter.class);

	private OfficeManager officeManager = null;;
	private DocumentFormatRegistry formatRegistry = null;
	private Map<String, ?> loadProperties = null;
	
	// EGAD THIS IS BURIED DEEPLY
	public FileConverter(OfficeManager officeManager) {
		this.officeManager = officeManager;
		
		// A bit inefficient
		OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
		this.formatRegistry = converter.getFormatRegistry();
		this.loadProperties = converter.getDefaultLoadProperties();
	}
	
	public ConversionResult convert(File input) {
		ConversionResult result = new ConversionResult();
		
		try {
			String baseName = FilenameUtils.getBaseName(input.getName());
			File tempdir    = FileUtils.getTempDirectory();
			File outputFile = new File(tempdir, baseName + "_conv");
			
	        DoublePDFTask task = new DoublePDFTask(input, outputFile, this.formatRegistry, this.loadProperties);
	        officeManager.execute(task);
			
			// create open office file
			result.thumbFile = task.thumbnail;
			result.fullFile  = task.allPagePdf;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			result.errorMessage = sw.toString(); 
			logger.warn("Error running open office", e);
		}
		return result;
	}

}

package com.mydocket.pdfmaker.converter;

import io.netty.channel.ChannelHandlerContext;

import java.io.File;
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
	
	private ThumbAndReturn thumbAndReturn = new ThumbAndReturn();
	
	// EGAD THIS IS BURIED DEEPLY
	public FileConverter(OfficeManager officeManager) {
		this.officeManager = officeManager;
		
		// A bit inefficient
		OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
		this.formatRegistry = converter.getFormatRegistry();
		this.loadProperties = converter.getDefaultLoadProperties();
	}
	
	public void convert(ChannelHandlerContext ctx, File input) {
		Observer observer = new Observer(ctx);
		try {
			String baseName = FilenameUtils.getBaseName(input.getName());
			String extension = FilenameUtils.getExtension(input.getName());
			
			if (extension.equalsIgnoreCase("PDF")) {
				// do not try to convert it
				thumbAndReturn.execute(observer, input);
			} else {
				File tempdir    = FileUtils.getTempDirectory();
				File outputFile = new File(tempdir, baseName + "_conv");
				
		        DoublePDFTask task = new DoublePDFTask(observer, input, outputFile, this.formatRegistry, this.loadProperties);
		        officeManager.execute(task);
			}
		} catch (Exception e) {
			// TODO: possible to append this after we've written out both files
			// successfully.  Need to test.
			observer.observe(e);
			logger.warn("Error running open office", e);
		} finally {
			observer.finish();
		}
	}

}

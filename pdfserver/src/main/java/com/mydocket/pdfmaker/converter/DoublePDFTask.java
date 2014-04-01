package com.mydocket.pdfmaker.converter;

import static org.artofsolving.jodconverter.office.OfficeUtils.cast;
import static org.artofsolving.jodconverter.office.OfficeUtils.toUnoProperties;
import static org.artofsolving.jodconverter.office.OfficeUtils.toUrl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.artofsolving.jodconverter.OfficeDocumentUtils;
import org.artofsolving.jodconverter.StandardConversionTask;
import org.artofsolving.jodconverter.document.DocumentFamily;
import org.artofsolving.jodconverter.document.DocumentFormat;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;
import org.artofsolving.jodconverter.office.OfficeException;

import com.sun.star.frame.XStorable;
import com.sun.star.io.IOException;
import com.sun.star.lang.XComponent;
import com.sun.star.task.ErrorCodeIOException;

public class DoublePDFTask extends StandardConversionTask {

	// TODO: slf4j?
    private final Logger logger = Logger.getLogger(getClass().getName());
	
	private DocumentFormat allPageFormat = AllPagesFormat.getInstance();
	private DocumentFormat onePageFormat = SinglePageFormat.getInstance();

	private File baseOutputFile;
	
	public File onePagePdf;
	public File thumbnail;
	public File allPagePdf;
	
	
	
	/**
	 * input file = the item to convert to OO
	 * outputFile = the base file name that we're going to write to.
	 * 
	 * NB that super(outputFormat) is ignored, as we will use two different formats.
	 * 
	 * @param inputFile
	 * @param outputFile
	 */
	public DoublePDFTask(File inputFile, File outputFile, DocumentFormatRegistry formatRegistry, Map<String, ?> loadProperties) {
		super(inputFile, outputFile, null);
		this.baseOutputFile = outputFile;
		
        String inputExtension = FilenameUtils.getExtension(inputFile.getName());
        DocumentFormat inputFormat = formatRegistry.getFormatByExtension(inputExtension);
		
		setDefaultLoadProperties(loadProperties);
		setInputFormat(inputFormat);
	}

	@Override
    protected void modifyDocument(XComponent document) throws OfficeException {
        Map<String,?> firstPageOnly = getStoreProperties(this.onePageFormat, document);
        this.onePagePdf = getExtensionFile(this.baseOutputFile, "_onepage.pdf");
        storeDocument(document, firstPageOnly, this.onePagePdf);
        
        // now, let's also imagemagick that stuff
        try {
        	this.thumbnail = doThumb(this.baseOutputFile, this.onePagePdf);
        } catch (Exception e) {
        	throw new OfficeException("Error generating thumbnail", e);
        }
        
    }

	protected File doThumb(File baseFileName, File source) throws Exception {
		File thumb = getExtensionFile(baseFileName, "_thumb.png");
		// convert "$outpdf[0]" -flatten -resize "150x150" -colorspace 'rgb' $outjpg 2>/dev/null
		String[] cmd = {
				"convert",
				source.getAbsolutePath() + "[0]",
				"-flatten",
				"-resize", "150x150",
				"-colorspace", "rgb",
				thumb.getAbsolutePath()
		};
		
		logger.finest("Converter command: " + cmd);
		
		// if you get a "can't find convert", make sure to add /usr/local/bin to the path
		ProcessBuilder pb = new ProcessBuilder(cmd);
		Process p = pb.start();
		
		// consume both output and stderr
		String stdout = this.consumeStream(p.getInputStream(), "stdout");	// why is it called InputStream when it's really stdout?
		String stderr = this.consumeStream(p.getErrorStream(), "stderr");	

        p.waitFor();
		if (p.exitValue() != 0) {
			logger.warning("Thumbnail exited with non-zero status: " + p.exitValue());
			logger.warning("Stderr is " + stderr);
			logger.warning("Stdout is " + stdout);
		}
		return thumb;
	}
	
	private String consumeStream(InputStream is, String name) throws Exception {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
        br.close();
		String rv = sb.toString();
		if (rv.length() > 0) {
			logger.finest("Stream '" + name + "': " + rv);
		}
		
		return rv;
	}
	
	
    @Override
    protected void storeDocument(XComponent document, File outputFile) throws OfficeException {
        Map<String,?> fullPdf = getStoreProperties(this.allPageFormat, document);
       this.allPagePdf = getExtensionFile(this.baseOutputFile, "_full.pdf");
    	storeDocument(document, fullPdf, this.allPagePdf);
    }
    
    protected void storeDocument(XComponent document, Map<String, ?> storeProperties, File outputFile) throws OfficeException {
        if (storeProperties == null) {
            throw new OfficeException("unsupported conversion");
        }
        try {
            cast(XStorable.class, document).storeToURL(toUrl(outputFile), toUnoProperties(storeProperties));
        } catch (ErrorCodeIOException errorCodeIOException) {
            throw new OfficeException("could not store document: " + outputFile.getName() + "; errorCode: " + errorCodeIOException.ErrCode, errorCodeIOException);
        } catch (IOException ioException) {
            throw new OfficeException("could not store document: " + outputFile.getName(), ioException);
        }
    }
    
    // NB: different than the parent getStoreProperties
    private Map<String, ?> getStoreProperties(DocumentFormat format, XComponent source) {
        DocumentFamily family = OfficeDocumentUtils.getDocumentFamily(source);
        return format.getStoreProperties(family);
    }
    
    private File getExtensionFile(File base, String extension) {
    	File absFile = base.getAbsoluteFile();
    	File newFile = new File(absFile.getAbsolutePath() + extension);
    	return newFile;
    }
}

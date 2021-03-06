package com.mydocket.pdfserver.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MakeAThumb {
	
	
	private static final Logger logger = LoggerFactory
			.getLogger(MakeAThumb.class);

	@SuppressWarnings("unused")
	public File doThumb(File baseFileName, File source) throws Exception {
		File thumb = getExtensionFile(baseFileName, "_thumb.png");
		// convert "$outpdf[0]" -flatten -resize "150x150" -colorspace 'rgb' $outjpg 2>/dev/null
		String[] cmd = null;

        // this is to make a 150x150.  Quite fuzzy when you scale up
        if (false) {
            cmd = new String[] {
                    "convert",
                    source.getAbsolutePath() + "[0]",
                    "-flatten",
                    "-resize", "150x150",
                    "-colorspace", "rgb",
                    thumb.getAbsolutePath()
            };
        } else {
        // export the first page as native size
        cmd = new String[] {
                    "convert",
                    source.getAbsolutePath() + "[0]",
                    "-flatten",
                    thumb.getAbsolutePath()
            };
        }
		
        if (logger.isDebugEnabled()) {
        	StringBuilder sb = new StringBuilder();
        	for (String s : cmd) {
        		if (sb.length() > 0 ) {
        			sb.append(" ");
        		}
        		sb.append(s);
        	}
        	logger.debug("Converter command: " + sb.toString());
        }
		// if you get a "can't find convert", make sure to add /usr/local/bin to the path
		ProcessBuilder pb = new ProcessBuilder(cmd);
		Process p = pb.start();
		
		// consume both output and stderr
		String stdout = this.consumeStream(p.getInputStream(), "stdout");	// why is it called InputStream when it's really stdout?
		String stderr = this.consumeStream(p.getErrorStream(), "stderr");	

        p.waitFor();
		if (p.exitValue() != 0) {
			logger.warn("Thumbnail exited with non-zero status: " + p.exitValue());
			logger.warn("Stderr is " + stderr);
			logger.warn("Stdout is " + stdout);
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
			logger.trace("Stream '" + name + "': " + rv);
		}
		
		return rv;
	}
	
    public File getExtensionFile(File base, String extension) {
    	File absFile = base.getAbsoluteFile();
    	File newFile = new File(absFile.getAbsolutePath() + extension);
    	return newFile;
    }
	
	
}

package com.mydocket.pdfserver.converter;

import java.io.File;

// When OO was importing a PDF, it would try to convert it into a giant
// list of postscript pages.  As in: actual code.
// So if you get a PDF, just run the thumbnail code and return
// the original file.
public class ThumbAndReturn {

	private MakeAThumb makeAThumb = new MakeAThumb();
	
	public void execute(Observer observer, File input) throws Exception {
		File thumb = makeAThumb.doThumb(input, input);
		observer.observe(thumb);
		observer.observe(input);
	}
	
}

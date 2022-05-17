package org.openxava.test.model;

import org.apache.commons.fileupload.*;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */
public class ShowTextFile {
	
	@Editor("TextFile")
	private FileItem textFile;

	public FileItem getTextFile() {
		return textFile;
	}

	public void setTextFile(FileItem textFile) {
		this.textFile = textFile;
	}

}

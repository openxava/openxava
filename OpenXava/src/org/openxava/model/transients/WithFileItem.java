package org.openxava.model.transients;

import org.apache.commons.fileupload.*;
import org.openxava.annotations.*;

/**
 * To use as generic transient class for dialogs.
 * 
 * @since 6.2
 * @author Javier Paniza
 */
public class WithFileItem implements java.io.Serializable {

	@LabelFormat(LabelFormatType.NO_LABEL)
	private FileItem file; 

	public FileItem getFile() {
		return file;
	}

	public void setFile(FileItem file) {
		this.file = file;
	}

}

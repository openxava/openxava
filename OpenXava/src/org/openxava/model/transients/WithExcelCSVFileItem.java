package org.openxava.model.transients;

import org.apache.commons.fileupload.*;
import org.openxava.annotations.*;

/**
 * tmr
 * To use as generic transient class for dialogs.
 * 
 * @since 6.6
 * @author Javier Paniza
 */
public class WithExcelCSVFileItem implements java.io.Serializable {

	@LabelFormat(LabelFormatType.NO_LABEL)
	@FileItemUpload(acceptFileTypes = "text/csv, application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	private FileItem file; 

	public FileItem getFile() {
		return file;
	}

	public void setFile(FileItem file) {
		this.file = file;
	}

}

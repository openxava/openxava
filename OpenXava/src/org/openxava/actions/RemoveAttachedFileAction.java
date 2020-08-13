package org.openxava.actions;

import org.openxava.web.editors.*;

/**
 * Logic of AttachedFileEditor.remove action in default-controllers.xml. <p> 
 * 
 * It does not remove the file from the file container. <p> 
 * 
 * @author Jeromy Altuna
 * @author Javier Paniza
 */
public class RemoveAttachedFileAction extends ViewBaseAction {
	
	private String fileId; 
	private String property;  
	
	public void execute() throws Exception {
		getView().setValue(getProperty(), null);
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
}

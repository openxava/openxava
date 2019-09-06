package org.openxava.actions;

import org.openxava.web.editors.*;

/**
 * tmp ¿Redoc?
 * Logic of AttachedFile.delete action in default-controllers.xml. <p> 
 * 
 * It does not remove the file from the file container. <p>
 * 
 * @author Jeromy Altuna
 */
public class RemoveAttachedFileAction extends ViewBaseAction {
	
	// tmp private String newFileProperty;
	private String fileId; // tmp
	private String property; // tmp 
	
	public void execute() throws Exception {
		// tmp ini
		getView().setValue(getProperty(), null);
		// tmp fin		
		// tmp getView().setValue(getNewFileProperty(), null);		
	}

	/* tmp
	public String getNewFileProperty() {
		return newFileProperty;
	}

	public void setNewFileProperty(String newFileProperty) {
		this.newFileProperty = newFileProperty;
	}
	*/

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

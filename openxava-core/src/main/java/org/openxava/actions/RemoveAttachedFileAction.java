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
public class RemoveAttachedFileAction extends AttachedFileBaseAction {
	
	private String fileId; 
	
	public void execute() throws Exception {
		getView().setValue(getProperty(), null);
		// We don't remove the file from database because the user could remove the file and leave the record without saving
		//   so we'll have a field with id pointing to an existent file.
		AttachedFile file = FilePersistorFactory.getInstance().find(getFileId());
		trackModification(file, "removed_from_file_property");
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

}

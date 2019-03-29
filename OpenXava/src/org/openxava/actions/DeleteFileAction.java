package org.openxava.actions;

/**
 * Logic of AttachedFile.delete action in default-controllers.xml. <p> 
 * 
 * It does not remove the file from the file container. <p>
 * 
 * @author Jeromy Altuna
 */
public class DeleteFileAction extends ViewBaseAction {
	
	private String newFileProperty;
	
	public void execute() throws Exception {
		getView().setValue(getNewFileProperty(), null);		
	}

	public String getNewFileProperty() {
		return newFileProperty;
	}

	public void setNewFileProperty(String newFileProperty) {
		this.newFileProperty = newFileProperty;
	}
}

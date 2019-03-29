package org.openxava.actions;

import javax.inject.*;

/**
 * Logic of AttachedFile.choose action in default-controllers.xml. <p>
 * 
 * @author Jeromy Altuna
 */
public class ChooseFileAction extends ViewBaseAction implements ILoadFileAction {
	
	@Inject
	private String newFileProperty;	
	
	public String[] getNextControllers() throws Exception {
		return new String [] { "UploadFile" };
	}

	public void execute() throws Exception {
		showDialog();
	}

	public String getCustomView() throws Exception {
		return "xava/editors/chooseFile";
	}

	public boolean isLoadFile() {
		return true;
	}

	public String getNewFileProperty() {
		return newFileProperty;
	}

	public void setNewFileProperty(String newFileProperty) {
		this.newFileProperty = newFileProperty;
	}		
}

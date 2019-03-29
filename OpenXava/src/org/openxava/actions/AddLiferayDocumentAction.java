package org.openxava.actions;


/**
 * 
 * @author Oscar Caro
 */
public class AddLiferayDocumentAction extends BaseAction implements ILoadFileAction{

	
	public boolean isLoadFile() {
		return true;
	}

	public String[] getNextControllers() throws Exception {
		return new String [] { "LoadLiferayDocument" }; 
	}

	public void execute() throws Exception {
		
	}

	public String getCustomView() throws Exception {
		return "xava/editors/liferayUploadFile";
	}


}

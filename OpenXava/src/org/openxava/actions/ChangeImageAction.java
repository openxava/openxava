package org.openxava.actions;

import javax.inject.*;




/**
 * @author Javier Paniza
 */

public class ChangeImageAction extends ViewBaseAction implements ILoadFileAction { 
		
	@Inject
	private String newImageProperty; 	
	
	public void execute() throws Exception {
		showDialog(); 
	}

	public String[] getNextControllers() {		
		return new String [] { "LoadImage" }; 
	}

	public String getCustomView() {		
		return "xava/editors/changeImage";
	}

	public boolean isLoadFile() {		
		return true;
	}

	public String getNewImageProperty() {
		return newImageProperty;
	}

	public void setNewImageProperty(String string) {
		newImageProperty = string;		
	}

}

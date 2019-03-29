package org.openxava.actions;

import java.util.*;
import javax.inject.*;
import org.apache.commons.fileupload.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */

public class LoadImageAction extends ViewBaseAction implements INavigationAction, IProcessLoadedFileAction {
	
	private List fileItems;
	@Inject
	private String newImageProperty;
	

	public void execute() throws Exception {
		Iterator i = getFileItems().iterator();
		while (i.hasNext()) {
			FileItem fi = (FileItem)i.next();
			String fileName = fi.getName();			
			if (!Is.emptyString(fileName)) {
				getPreviousView().setValue(getNewImageProperty(), fi.get()); 
			}			
		}		
		closeDialog(); 
	}
	
	public String[] getNextControllers() {				
		return PREVIOUS_CONTROLLERS;
	}

	public String getCustomView() {		
		return PREVIOUS_VIEW;
	}

	public String getNewImageProperty() {
		return newImageProperty;
	}

	public void setNewImageProperty(String string) {
		newImageProperty = string;	
	}

	public List getFileItems() {
		return fileItems;
	}

	public void setFileItems(List fileItems) {
		this.fileItems = fileItems;
	}

}

package org.openxava.actions;

import java.util.*;
import org.apache.commons.fileupload.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */

public class LoadImageAction extends ViewBaseAction implements INavigationAction, IProcessLoadedFileAction {
	
	private List fileItems;
	// tmp private String newImageProperty;
	private String property; // tmp
	
	public void execute() throws Exception {
		Iterator i = getFileItems().iterator();
		while (i.hasNext()) {
			FileItem fi = (FileItem)i.next();
			String fileName = fi.getName();			
			if (!Is.emptyString(fileName)) { 
				// tmp getView().setValue(getNewImageProperty(), fi.get()); 
				getView().setValue(property, fi.get()); // tmp
			}			
		}		
	}
	
	public String[] getNextControllers() {				
		return PREVIOUS_CONTROLLERS;
	}

	public String getCustomView() {		
		return PREVIOUS_VIEW;
	}

	/* tmp
	public String getNewImageProperty() {
		return newImageProperty;
	}

	public void setNewImageProperty(String string) {
		newImageProperty = string;	
	}
	*/

	public List getFileItems() {
		return fileItems;
	}

	public void setFileItems(List fileItems) {
		this.fileItems = fileItems;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

}

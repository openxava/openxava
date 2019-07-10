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
	// tmp @Inject
	private String newImageProperty;
	

	public void execute() throws Exception {
		Iterator i = getFileItems().iterator();
		while (i.hasNext()) {
			FileItem fi = (FileItem)i.next();
			String fileName = fi.getName();			
			System.out.println("[LoadImageAction.execute] fileName=" + fileName); // tmp
			if (!Is.emptyString(fileName)) {
				// tmp getPreviousView().setValue(getNewImageProperty(), fi.get()); 
				getView().setValue(getNewImageProperty(), fi.get()); // tmp ¿En migration? ¿Cómo documentar (o afrontar) este cambio?
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

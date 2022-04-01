package org.openxava.actions;

import java.util.*;
import org.apache.commons.fileupload.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class LoadImageAction extends ViewBaseAction implements IProcessLoadedFileAction {
	
	private List fileItems;
	private String property; 
	
	public void execute() throws Exception {
		Iterator i = getFileItems().iterator();
		while (i.hasNext()) {
			FileItem fi = (FileItem)i.next();
			String fileName = fi.getName();			
			if (!Is.emptyString(fileName)) { 
				try {
					getView().setValue(property, fi.get());
				}
				catch (ElementNotFoundException ex) { 
					// It can happen in @ElementCollection
				}
			}			
		}		
	}
	
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

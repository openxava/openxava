package org.openxava.actions;

import java.util.*;

import org.apache.commons.fileupload.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class LoadFileItemAction extends ViewBaseAction implements IProcessLoadedFileAction {
	
	private List fileItems;
	private String property; 
	
	public void execute() throws Exception {
		Iterator i = getFileItems().iterator();
		while (i.hasNext()) {
			FileItem fi = (FileItem)i.next();
			String fileName = fi.getName();			
			if (!Is.emptyString(fileName)) {
				getView().setValue(property, fi); 
				break;
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


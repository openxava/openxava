package org.openxava.actions;

import java.util.*;


import org.apache.commons.fileupload.*;
import org.openxava.util.*;
import org.openxava.web.editors.*;

/**
 * Logic of AttachedFileEditor.load action in default-controllers.xml. <p>
 * 
 * Instance {@link org.openxava.web.editors.AttachedFile} object and stores it 
 * in a file container. <p>
 * 
 * @author Jeromy Altuna
 */
public class LoadAttachedFileAction extends ViewBaseAction implements IProcessLoadedFileAction {
	
	@SuppressWarnings("rawtypes")
	private List fileItems;	
	private String property; 
		
	public void execute() throws Exception {
		Iterator<?> it = getFileItems().iterator();
		while (it.hasNext()) {
			FileItem fi = (FileItem) it.next();					
			if (!Is.emptyString(fi.getName())) {
				AttachedFile file = new AttachedFile();
				file.setName(fi.getName());
				file.setData(fi.get());
				FilePersistorFactory.getInstance().save(file);
				getView().setValue(property, file.getId()); 
				break;
			}			
		}		
	}
	
	@SuppressWarnings("rawtypes")
	public List getFileItems() {
		return fileItems;
	}

	@SuppressWarnings("rawtypes")
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

package org.openxava.actions;

import java.util.*;

import javax.inject.*;
import javax.servlet.http.*;

import org.apache.commons.fileupload.*;
import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.web.*;
import org.openxava.web.editors.*;

/**
 * Logic of UploadFile.uploadFile action in default-controllers.xml. <p>
 * 
 * Instance {@link org.openxava.web.editors.AttachedFile} object and stores it 
 * in a file container. <p>
 * 
 * @author Jeromy Altuna
 */
public class LoadAttachedFileAction extends ViewBaseAction implements /* tmp INavigationAction, */ 
																IProcessLoadedFileAction 
{
	
	@SuppressWarnings("rawtypes")
	private List fileItems;
	
	/* tmp
	@Inject
	private String newFileProperty;
	*/
	
	private String property; // tmp
		
	public void execute() throws Exception {
		Iterator<?> it = getFileItems().iterator();
		while (it.hasNext()) {
			FileItem fi = (FileItem) it.next();					
			if (!Is.emptyString(fi.getName())) {
				AttachedFile file = new AttachedFile();
				file.setName(fi.getName());
				file.setData(fi.get());
				FilePersistorFactory.getInstance().save(file);
				// tmp getPreviousView().setValue(getNewFileProperty(), file.getId());
				System.out.println("[UploadFileAction.execute] property=" + property); // tmp
				System.out.println("[UploadFileAction.execute] file.getId()=" + file.getId()); // tmp
				getView().setValue(property, file.getId()); // tmp
				break;
			}			
		}		
		// tmp closeDialog(); 
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

	/* tmp
	public String[] getNextControllers() throws Exception {
		return PREVIOUS_CONTROLLERS;
	}

	public String getCustomView() throws Exception {
		return PREVIOUS_VIEW;
	}

	public String getNewFileProperty() {
		return newFileProperty;
	}

	public void setNewFileProperty(String newFileProperty) {
		this.newFileProperty = newFileProperty;
	}	
	*/
	
}

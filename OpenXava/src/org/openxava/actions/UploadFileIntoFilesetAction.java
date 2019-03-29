package org.openxava.actions;

import java.util.*;

import javax.inject.*;

import org.apache.commons.fileupload.*;
import org.openxava.util.*;
import org.openxava.web.editors.*;

/**
 * Logic of UploadFileIntoFileset.uploadFile action in default-controllers.xml. <p>
 * 
 * @author Jeromy Altuna
 */
public class UploadFileIntoFilesetAction extends    ViewBaseAction 
										 implements INavigationAction, 
										 			IProcessLoadedFileAction 
{
	
	@SuppressWarnings("rawtypes")
	private List fileItems;
	
	@Inject
	private String newFilesetProperty;
	
	@Override
	public void execute() throws Exception {
		String libraryId = getPreviousView().getValueString(newFilesetProperty);
		Iterator<?> it = fileItems.iterator();		
		int counter = 0;
		StringBuffer filesNames = new StringBuffer(); 
		while(it.hasNext()) {
			FileItem fi = (FileItem) it.next();
			if(!Is.emptyString(fi.getName())) {
				AttachedFile file = new AttachedFile();
				file.setName(fi.getName());
				file.setLibraryId(libraryId);
				file.setData(fi.get());
				FilePersistorFactory.getInstance().save(file);
				counter++;
				if (filesNames.length() > 0) filesNames.append(", ");
				filesNames.append(fi.getName());
			}
		}
		if(counter == 1) addMessage("file_added_to_fileset", newFilesetProperty);
		else if(counter > 1) addMessage("files_added_to_fileset", 
										newFilesetProperty, counter);
		
		trackModification(filesNames.toString()); 
		closeDialog();
	}
	
	private void trackModification(String fileName) {  
		Map oldChangedValues = new HashMap();
		oldChangedValues.put(newFilesetProperty, XavaResources.getString("files_files_added"));  
		Map newChangedValues = new HashMap();
		newChangedValues.put(newFilesetProperty, fileName); 
		AccessTracker.modified(getPreviousView().getModelName(), getPreviousView().getKeyValues(), oldChangedValues, newChangedValues);
	}
	
	@Override
	public String[] getNextControllers() throws Exception {
		return PREVIOUS_CONTROLLERS;
	}	
	
	@Override
	public String getCustomView() throws Exception {
		return PREVIOUS_VIEW;
	}

	@Override @SuppressWarnings("rawtypes")
	public void setFileItems(List fileItems) {
		this.fileItems = fileItems;
	}
}

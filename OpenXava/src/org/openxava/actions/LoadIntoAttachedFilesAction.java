package org.openxava.actions;

import java.util.*;

import javax.inject.*;

import org.apache.commons.fileupload.*;
import org.openxava.calculators.*;
import org.openxava.model.*;
import org.openxava.util.*;
import org.openxava.web.editors.*;

/**
 * Logic of AttachedFilesEditor.load action in default-controllers.xml. <p>
 * 
 * @author Jeromy Altuna
 */
public class LoadIntoAttachedFilesAction extends GenerateIdForPropertyBaseAction  
										 implements IProcessLoadedFileAction 
{
	
	@SuppressWarnings("rawtypes")
	private List fileItems;
	
	private String property;
	
	@Override
	public void execute() throws Exception {
		String libraryId = generateIdForProperty(property);  
		Iterator<?> it = fileItems.iterator();		
		StringBuffer filesNames = new StringBuffer(); 
		while(it.hasNext()) {
			FileItem fi = (FileItem) it.next();
			if(!Is.emptyString(fi.getName())) {
				AttachedFile file = new AttachedFile();
				file.setName(fi.getName());
				file.setLibraryId(libraryId);
				file.setData(fi.get());
				FilePersistorFactory.getInstance().save(file);
				if (filesNames.length() > 0) filesNames.append(", ");
				filesNames.append(fi.getName());
			}
		}
		
		trackModification(filesNames.toString());
	}
	
	private void trackModification(String fileName) {  
		Map oldChangedValues = new HashMap();
		oldChangedValues.put(property, XavaResources.getString("files_files_added")); 
		Map newChangedValues = new HashMap();
		newChangedValues.put(property, fileName); 
		AccessTracker.modified(getView().getModelName(), getView().getKeyValues(), oldChangedValues, newChangedValues); 
	}
	
	@Override @SuppressWarnings("rawtypes")
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

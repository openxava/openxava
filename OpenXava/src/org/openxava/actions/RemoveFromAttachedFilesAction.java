package org.openxava.actions;

import java.util.*;

import org.openxava.util.*;
import org.openxava.web.editors.*;

/**
 * Logic of AttachedFiles.remove in default-controllers.xml. <p>
 * 
 * It remove the file of the file container. <p>
 * 
 * @author Jeromy Altuna
 * @author Javier Paniza
 */
public class RemoveFromAttachedFilesAction extends ViewBaseAction {
	
	private String property; 
	private String fileId;
	
	@Override
	public void execute() throws Exception {
		AttachedFile file = FilePersistorFactory.getInstance().find(getFileId()); 
		String libraryId = getView().getValueString(property);
		Collection library = FilePersistorFactory.getInstance().findLibrary(libraryId);
		if (getView().getMetaProperty(property).isRequired() && library.size() < 2) {
			getView().setValue(property, null);
		}
		else {
			FilePersistorFactory.getInstance().remove(getFileId());
		}

		trackModification(file); 
	}	
	
	private void trackModification(AttachedFile file) { 
		String property = (String) Maps.getKeyFromValue(getView().getValues(), file.getLibraryId(), "FILES"); 
		Map oldChangedValues = new HashMap();
		oldChangedValues.put(property, XavaResources.getString("files_file_removed"));  
		Map newChangedValues = new HashMap();
		newChangedValues.put(property, file.getName()); 
		AccessTracker.modified(getView().getModelName(), getView().getKeyValues(), oldChangedValues, newChangedValues);
	}


	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}
}

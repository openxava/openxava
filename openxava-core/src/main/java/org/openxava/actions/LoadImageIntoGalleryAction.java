package org.openxava.actions;

import java.util.*;
import org.apache.commons.fileupload.*;
import org.openxava.session.*;
import org.openxava.util.*;
import org.openxava.view.*;

/**
 * @author Javier Paniza
 */

public class LoadImageIntoGalleryAction extends GenerateIdForPropertyBaseAction implements IProcessLoadedFileAction { 

	private List fileItems;	
	private String property; 
	
	public void execute() throws Exception {	
		Iterator i = getFileItems().iterator();
		StringBuffer filesNames = new StringBuffer();
		String galleryOid = generateIdForProperty(property); 
		Gallery gallery = Gallery.find(galleryOid);
		while (i.hasNext()) {
			FileItem fi = (FileItem)i.next();				
			if (!Is.emptyString(fi.getName())) {
				gallery.addImage(fi.get());
				if (filesNames.length() > 0) filesNames.append(", ");
				filesNames.append(fi.getName());
			}			
		}		
		trackModification(galleryOid, filesNames.toString());
	}
		
	private void trackModification(String galleryOid, String fileName) { 
		View view = getView(); 
		String property = (String) Maps.getKeyFromValue(view.getValues(), galleryOid, "IMAGES_GALLERY"); 
		Map oldChangedValues = new HashMap();
		oldChangedValues.put(property, XavaResources.getString("images_gallery_images_added"));  
		Map newChangedValues = new HashMap();
		newChangedValues.put(property, fileName); 
		AccessTracker.modified(view.getModelName(), view.getKeyValues(), oldChangedValues, newChangedValues);
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

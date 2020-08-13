package org.openxava.actions;

import java.util.*;
import org.openxava.session.*;
import org.openxava.util.*;
import org.openxava.validators.*;
import org.openxava.view.*;

/**
 * 
 * @author Javier Paniza
 */

public class RemoveImageFromGalleryAction extends ViewBaseAction {
	
	private String fileId;
	private String property; 
	
	public void execute() throws Exception {
		String galleryOid = getView().getValueString(property);
		Gallery gallery = Gallery.find(galleryOid);
		if (getView().getMetaProperty(property).isRequired() && gallery.getImagesOids().size() < 2) {
			getView().setValue(property, null);
		}
		else {
			gallery.removeImage(fileId);
		}
		trackModification(galleryOid); 
	}
	
	private void trackModification(String galleryOid) {   
		View view = getPreviousViews().isEmpty()?getView():getPreviousView();
		String property = (String) Maps.getKeyFromValue(view.getValues(), galleryOid, "IMAGES GALLERY"); 
		Map oldChangedValues = new HashMap();
		oldChangedValues.put(property, XavaResources.getString("images_gallery_image_removed"));  
		Map newChangedValues = new HashMap();
		newChangedValues.put(property, XavaResources.getString("one_image_removed")); 
		AccessTracker.modified(view.getModelName(), view.getKeyValues(), oldChangedValues, newChangedValues);
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

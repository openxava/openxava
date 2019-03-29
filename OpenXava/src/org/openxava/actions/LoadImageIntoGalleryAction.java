package org.openxava.actions;

import java.util.*;

import javax.inject.*;

import org.apache.commons.fileupload.*;


import org.openxava.session.*;
import org.openxava.util.*;
import org.openxava.view.*;

/**
 * @author Javier Paniza
 */

public class LoadImageIntoGalleryAction extends ViewBaseAction implements INavigationAction, IProcessLoadedFileAction {

	private List fileItems;
	
	@Inject
	private Gallery gallery;
	

	public void execute() throws Exception {		
		Iterator i = getFileItems().iterator();
		int c = 0;
		StringBuffer filesNames = new StringBuffer(); 
		while (i.hasNext()) {
			FileItem fi = (FileItem)i.next();					
			if (!Is.emptyString(fi.getName())) {
				getGallery().addImage(fi.get());
				c++;
				if (filesNames.length() > 0) filesNames.append(", ");
				filesNames.append(fi.getName());
			}			
		}		
		if (c == 1)	addMessage("image_added_to_gallery");
		else if (c > 1) addMessage("images_added_to_gallery", new Integer(c));
		trackModification(filesNames.toString());
		closeDialog(); 
	}
	
	private void trackModification(String fileName) { 
		View view = getPreviousViews().get(Math.max(getPreviousViews().size() - 2, 0));
		String property = (String) Maps.getKeyFromValue(view.getValues(), getGallery().getOid(), "IMAGES_GALLERY"); 
		Map oldChangedValues = new HashMap();
		oldChangedValues.put(property, XavaResources.getString("images_gallery_images_added"));  
		Map newChangedValues = new HashMap();
		newChangedValues.put(property, fileName); 
		AccessTracker.modified(view.getModelName(), view.getKeyValues(), oldChangedValues, newChangedValues);
	}

	public String[] getNextControllers() {		
		return PREVIOUS_CONTROLLERS;
	}

	public String getCustomView() {		
		return PREVIOUS_VIEW; 
	}

	public List getFileItems() {
		return fileItems;
	}

	public void setFileItems(List fileItems) {
		this.fileItems = fileItems;
	}

	public Gallery getGallery() {
		return gallery;
	}

	public void setGallery(Gallery gallery) {
		this.gallery = gallery;
	}

}

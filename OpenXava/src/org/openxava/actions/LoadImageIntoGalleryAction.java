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
	
	/* tmp
	@Inject
	private Gallery gallery;
	*/
	
	private String property; // tmp
	
	public void execute() throws Exception {		
		Iterator i = getFileItems().iterator();
		int c = 0;
		StringBuffer filesNames = new StringBuffer();
		Gallery gallery = Gallery.find(getView().getValueString(property));
		while (i.hasNext()) {
			FileItem fi = (FileItem)i.next();					
			if (!Is.emptyString(fi.getName())) {
				// tmp getGallery().addImage(fi.get());
				gallery.addImage(fi.get());
				c++;
				if (filesNames.length() > 0) filesNames.append(", ");
				filesNames.append(fi.getName());
			}			
		}		
		if (c == 1)	addMessage("image_added_to_gallery");
		else if (c > 1) addMessage("images_added_to_gallery", new Integer(c));
		trackModification(filesNames.toString());
		// tmp closeDialog(); 
	}
	
	private void trackModification(String fileName) { 
		// tmp View view = getPreviousViews().get(Math.max(getPreviousViews().size() - 2, 0));
		/* tmp Tenemos que activarlo y ha de funcionar. ¿Hay algún test de prueba?
		View view = getView(); // tmp
		String property = (String) Maps.getKeyFromValue(view.getValues(), getGallery().getOid(), "IMAGES_GALLERY"); 
		Map oldChangedValues = new HashMap();
		oldChangedValues.put(property, XavaResources.getString("images_gallery_images_added"));  
		Map newChangedValues = new HashMap();
		newChangedValues.put(property, fileName); 
		AccessTracker.modified(view.getModelName(), view.getKeyValues(), oldChangedValues, newChangedValues);
		*/
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

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	/* tmp
	public Gallery getGallery() {
		return gallery;
	}

	public void setGallery(Gallery gallery) {
		this.gallery = gallery;
	}
	*/

}

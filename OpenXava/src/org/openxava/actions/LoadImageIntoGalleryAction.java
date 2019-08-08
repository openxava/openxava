package org.openxava.actions;

import java.util.*;

import javax.inject.*;

import org.apache.commons.fileupload.*;
import org.openxava.calculators.*;
import org.openxava.model.*;
import org.openxava.session.*;
import org.openxava.util.*;
import org.openxava.view.*;

/**
 * @author Javier Paniza
 */

public class LoadImageIntoGalleryAction extends ViewBaseAction implements /* tmp INavigationAction, */ IProcessLoadedFileAction { 

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
		String galleryOid = getGalleryOid();
		Gallery gallery = Gallery.find(galleryOid);
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
		trackModification(galleryOid, filesNames.toString());
		// tmp closeDialog(); 
	}
	
	public String getGalleryOid() throws Exception {
		String oid = getView().getValueString(property);
		if (Is.emptyString(oid)) {
			UUIDCalculator cal = new UUIDCalculator();  
			oid = (String) cal.calculate();
			getView().setValue(property, oid);
			if (!getView().isKeyEditable()) { // Modifying
				updateOidInObject(oid);
			}
		}
		return oid;
	}

	private void updateOidInObject(String oid) throws Exception {
		Map values = new HashMap();
		values.put(property, oid);
		MapFacade.setValuesNotTracking(getView().getModelName(), getView().getKeyValues(), values); 
	}
	
	private void trackModification(String galleryOid, String fileName) { 
		// tmp View view = getPreviousViews().get(Math.max(getPreviousViews().size() - 2, 0));
		View view = getView(); // tmp
		// tmp String property = (String) Maps.getKeyFromValue(view.getValues(), getGallery().getOid(), "IMAGES_GALLERY"); 
		String property = (String) Maps.getKeyFromValue(view.getValues(), galleryOid, "IMAGES_GALLERY"); // tmp
		Map oldChangedValues = new HashMap();
		oldChangedValues.put(property, XavaResources.getString("images_gallery_images_added"));  
		Map newChangedValues = new HashMap();
		newChangedValues.put(property, fileName); 
		AccessTracker.modified(view.getModelName(), view.getKeyValues(), oldChangedValues, newChangedValues);
	}

	/* tmp
	public String[] getNextControllers() {		
		return PREVIOUS_CONTROLLERS;
	}

	public String getCustomView() {		
		return PREVIOUS_VIEW; 
	}
	*/

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

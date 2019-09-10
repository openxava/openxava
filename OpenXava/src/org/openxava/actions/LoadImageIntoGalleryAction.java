package org.openxava.actions;

import java.util.*;
import org.apache.commons.fileupload.*;
import org.openxava.calculators.*;
import org.openxava.model.*;
import org.openxava.session.*;
import org.openxava.util.*;
import org.openxava.view.*;

/**
 * @author Javier Paniza
 */

public class LoadImageIntoGalleryAction extends /* tmp ViewBaseAction */ GenerateIdForPropertyBaseAction implements IProcessLoadedFileAction { 

	private List fileItems;	
	private String property; 
	
	public void execute() throws Exception {	
		Iterator i = getFileItems().iterator();
		int c = 0;
		StringBuffer filesNames = new StringBuffer();
		// tmp String galleryOid = getGalleryOid();
		String galleryOid = generateIdForProperty(property); // tmp
		Gallery gallery = Gallery.find(galleryOid);
		while (i.hasNext()) {
			FileItem fi = (FileItem)i.next();				
			if (!Is.emptyString(fi.getName())) {
				gallery.addImage(fi.get());
				c++;
				if (filesNames.length() > 0) filesNames.append(", ");
				filesNames.append(fi.getName());
			}			
		}		
		if (c == 1)	addMessage("image_added_to_gallery");
		else if (c > 1) addMessage("images_added_to_gallery", new Integer(c));
		trackModification(galleryOid, filesNames.toString());
	}
	
	/* tmp
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
	*/
	
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

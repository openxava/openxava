package org.openxava.actions;

import java.util.*;
import javax.inject.*;
import org.openxava.session.*;
import org.openxava.util.*;
import org.openxava.view.*;

/**
 * 
 * @author Javier Paniza
 */

public class RemoveImageFromGalleryAction extends ViewBaseAction {
	
	/* tmp
	@Inject
	private Gallery gallery;
	*/
	private String oid;
	private String galleryProperty; // tmp
	
	
	public void execute() throws Exception {
		Gallery gallery = Gallery.find(getView().getValueString(galleryProperty)); // tmp
		gallery.removeImage(oid);
		// tmp trackModification(); 
	}
	
	private void trackModification() {  
		/* tmp
		View view = getPreviousViews().isEmpty()?getView():getPreviousView();
		String property = (String) Maps.getKeyFromValue(view.getValues(), gallery.getOid(), "IMAGES GALLERY"); 
		Map oldChangedValues = new HashMap();
		oldChangedValues.put(property, XavaResources.getString("images_gallery_image_removed"));  
		Map newChangedValues = new HashMap();
		newChangedValues.put(property, XavaResources.getString("one_image_removed")); 
		AccessTracker.modified(view.getModelName(), view.getKeyValues(), oldChangedValues, newChangedValues);
		*/
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getGalleryProperty() {
		return galleryProperty;
	}

	public void setGalleryProperty(String galleryProperty) {
		this.galleryProperty = galleryProperty;
	}

}

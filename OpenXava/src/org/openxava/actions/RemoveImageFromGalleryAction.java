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
	private String oid;
	*/
	// tmp ini
	private String fileId;
	private String property; 
	// tmp fin
	
	public void execute() throws Exception {
		System.out.println("[RemoveImageFromGalleryAction.execute] fileId=" + fileId); // tmp
		System.out.println("[RemoveImageFromGalleryAction.execute] property=" + property); // tmp
		// tmp ini
		String galleryOid = getView().getValueString(property);
		Gallery gallery = Gallery.find(galleryOid);
		gallery.removeImage(fileId);
		// tmp fin
		// tmp gallery.removeImage(oid);
		trackModification(galleryOid); 
	}
	
	private void trackModification(String galleryOid) { // tmp String galleryOid  
		View view = getPreviousViews().isEmpty()?getView():getPreviousView();
		// tmp String property = (String) Maps.getKeyFromValue(view.getValues(), gallery.getOid(), "IMAGES GALLERY"); 
		String property = (String) Maps.getKeyFromValue(view.getValues(), galleryOid, "IMAGES GALLERY"); // tmp
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

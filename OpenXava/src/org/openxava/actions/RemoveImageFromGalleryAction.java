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
	
	@Inject
	private Gallery gallery;
	private String oid;
	
	
	public void execute() throws Exception {
		gallery.removeImage(oid);
		trackModification(); 
	}
	
	private void trackModification() {  
		View view = getPreviousViews().isEmpty()?getView():getPreviousView();
		String property = (String) Maps.getKeyFromValue(view.getValues(), gallery.getOid(), "IMAGES GALLERY"); 
		Map oldChangedValues = new HashMap();
		oldChangedValues.put(property, XavaResources.getString("images_gallery_image_removed"));  
		Map newChangedValues = new HashMap();
		newChangedValues.put(property, XavaResources.getString("one_image_removed")); 
		AccessTracker.modified(view.getModelName(), view.getKeyValues(), oldChangedValues, newChangedValues);
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

}

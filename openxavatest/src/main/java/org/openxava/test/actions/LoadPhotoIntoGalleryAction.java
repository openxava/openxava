package org.openxava.test.actions;

import java.util.*;

import org.apache.commons.fileupload.*;
import org.openxava.actions.*;
import org.openxava.session.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class LoadPhotoIntoGalleryAction extends ViewBaseAction implements INavigationAction, IProcessLoadedFileAction {

	private List fileItems;
	
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
		String oid = getView().getValueString("photos");
		return Gallery.find(oid);
	}

}
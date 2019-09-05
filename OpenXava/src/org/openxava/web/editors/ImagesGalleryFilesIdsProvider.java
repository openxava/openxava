package org.openxava.web.editors;

import org.openxava.session.*;

/**
 * tmp
 * 
 * @author Javier Paniza
 */

public class ImagesGalleryFilesIdsProvider implements IUploadFilesIdsProvider {

	public String getFilesIds(Object propertyValue) {
		Gallery gallery = Gallery.find((String) propertyValue);
		StringBuilder filesIds = new StringBuilder();
		for (String imageOid: gallery.getImagesOids()) {
			if (filesIds.length() > 0) filesIds.append(',');
			filesIds.append(imageOid);
		}
		return filesIds.toString();
	}

}

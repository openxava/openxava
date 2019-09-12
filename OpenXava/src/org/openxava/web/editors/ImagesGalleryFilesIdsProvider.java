package org.openxava.web.editors;

import org.openxava.session.*;

/**
 * 
 * @since 6.2
 * @author Javier Paniza
 */

public class ImagesGalleryFilesIdsProvider implements IUploadFilesIdsProvider {

	public String getFilesIds(Object propertyValue) {
		return String.join(",", Gallery.find((String) propertyValue).getImagesOids()); 
	}

}

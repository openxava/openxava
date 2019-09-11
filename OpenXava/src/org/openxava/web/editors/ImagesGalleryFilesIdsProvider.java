package org.openxava.web.editors;

import java.util.*;
import java.util.stream.*;

import org.openxava.session.*;

/**
 * tmp
 * 
 * @author Javier Paniza
 */

public class ImagesGalleryFilesIdsProvider implements IUploadFilesIdsProvider {

	public String getFilesIds(Object propertyValue) {
		/* tmp
		Gallery gallery = Gallery.find((String) propertyValue);
		StringBuilder filesIds = new StringBuilder();
		for (String imageOid: gallery.getImagesOids()) {
			if (filesIds.length() > 0) filesIds.append(',');
			filesIds.append(imageOid);
		}
		return filesIds.toString();
		*/
		return String.join(",", Gallery.find((String) propertyValue).getImagesOids()); // tmp
	}

}

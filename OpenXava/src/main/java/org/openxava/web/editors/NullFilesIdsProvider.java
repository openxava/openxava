package org.openxava.web.editors;

/**
 * 
 * @since 6.2 
 * @author Javier Paniza
 */

public class NullFilesIdsProvider implements IUploadFilesIdsProvider {

	public String getFilesIds(Object propertyValue) {
		return null;
	}

}

package org.openxava.web.editors;

/**
 * tmp
 *  
 * @author Javier Paniza
 */

public class PropertyValueFilesIdsProvider implements IUploadFilesIdsProvider {

	public String getFilesIds(Object propertyValue) {
		return propertyValue == null?"":propertyValue.toString();
	}

}

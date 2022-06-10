package org.openxava.web.editors;

/**
 *  
 * @sincd 6.2 
 * @author Javier Paniza
 */

public class PropertyValueFilesIdsProvider implements IUploadFilesIdsProvider {

	public String getFilesIds(Object propertyValue) {
		return propertyValue == null?"":propertyValue.toString();
	}

}

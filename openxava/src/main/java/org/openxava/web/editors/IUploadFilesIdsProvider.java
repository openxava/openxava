package org.openxava.web.editors;

/**
 * For upload editors provides the list of oids from the property value.
 * 
 * @since 6.2
 * @author Javier Paniza
 */
public interface IUploadFilesIdsProvider {
	
	String getFilesIds(Object propertyValue);

}

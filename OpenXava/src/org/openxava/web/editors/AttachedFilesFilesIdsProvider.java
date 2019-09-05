package org.openxava.web.editors;

import java.util.*;

/**
 * tmp
 * 
 * @author Javier Paniza
 */
public class AttachedFilesFilesIdsProvider implements IUploadFilesIdsProvider {

	public String getFilesIds(Object propertyValue) {
		// tmp ¿Probar con streams?
		StringBuilder filesIds = new StringBuilder(); 
		Collection<AttachedFile> files = FilePersistorFactory.getInstance().findLibrary((String) propertyValue);
		for (AttachedFile file : files) {
			if (filesIds.length() > 0) filesIds.append(',');
			filesIds.append(file.getId());
		}
		return filesIds.toString();
	}

}

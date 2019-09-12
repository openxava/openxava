package org.openxava.web.editors;

import java.util.*;
import java.util.stream.*;

/**
 *
 * @since 6.2
 * @author Javier Paniza
 */
public class AttachedFilesFilesIdsProvider implements IUploadFilesIdsProvider {

	public String getFilesIds(Object propertyValue) {
		Collection<AttachedFile> files = FilePersistorFactory.getInstance().findLibrary((String) propertyValue);
		return files.stream().map(AttachedFile::getId).collect(Collectors.joining(","));
	}

}

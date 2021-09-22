package org.openxava.web.editors;

import java.util.*;
import java.util.stream.*;

import org.openxava.util.*;

/**
 *
 * @since 6.2
 * @author Javier Paniza
 */
public class AttachedFilesFilesIdsProvider implements IUploadFilesIdsProvider {

	public String getFilesIds(Object propertyValue) {
		Collection<AttachedFile> files = FilePersistorFactory.getInstance().findLibrary((String) propertyValue);
		return files.stream()
			.sorted((a, b) -> previewIndex(a) - previewIndex(b))
			.map(AttachedFile::getId).collect(Collectors.joining(","));
	}

	private int previewIndex(AttachedFile f) {
		return Files.isImage(f.getName())?1:0;  
	}

}

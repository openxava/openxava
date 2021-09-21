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
		return files.stream()
			.sorted((a, b) -> previewIndex(a) - previewIndex(b))
			.map(AttachedFile::getId).collect(Collectors.joining(","));
	}

	private int previewIndex(AttachedFile f) {
		if (f.getName().endsWith(".png") || 
			f.getName().endsWith(".jpg") ||
			f.getName().endsWith(".jpg") ||
			f.getName().endsWith(".bmp") ||
			f.getName().endsWith(".gif") ||
			f.getName().endsWith(".ico") ||
			f.getName().endsWith(".tiff") ||
			f.getName().endsWith(".tif"))
		{
			return 1;
		}
		return 0;
	}

}

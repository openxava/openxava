package org.openxava.actions;


import javax.inject.*;
import org.openxava.session.*;

import com.liferay.portlet.documentlibrary.service.*;

/**
 * 
 * @author Oscar Caro 
 */
public class DeleteLiferayDocumentAction extends BaseAction {

	private String libraryDocumentFileEntryId;
	
	@Inject
	private LiferayLibrary liferayLibrary;
	
	public void execute() throws Exception {
		//If alsoDeleteFile is set to true in editors.xml, we delete the document from the Document Library
		if (liferayLibrary.isAlsoDeleteFile()){
			DLFileEntryLocalServiceUtil.deleteDLFileEntry(liferayLibrary.getLRDocument(libraryDocumentFileEntryId));
		}
		//Remove link 
		liferayLibrary.removeDocument(libraryDocumentFileEntryId);		
	}

	public String getLibraryDocumentFileEntryId() {
		return libraryDocumentFileEntryId;
	}

	public void setLibraryDocumentFileEntryId(String libraryDocumentFileEntryId) {
		this.libraryDocumentFileEntryId = libraryDocumentFileEntryId;
	}


}

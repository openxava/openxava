package org.openxava.actions;


import java.io.*;
import java.util.*;

import javax.inject.*;

import org.apache.commons.fileupload.*;
import org.openxava.calculators.*;
import org.openxava.session.*;
import org.openxava.util.*;
import org.openxava.view.*;

import com.liferay.portal.kernel.dao.orm.*;
import com.liferay.portal.kernel.util.*;
import com.liferay.portal.service.*;
import com.liferay.portlet.documentlibrary.model.*;
import com.liferay.portlet.documentlibrary.service.*;

/**
 * 
 * @author Oscar Caro 
 */
public class LoadLiferayDocumentAction extends BaseAction implements
		INavigationAction, IProcessLoadedFileAction {

	private List fileItems;
	private View view;
	
	@Inject
	private LiferayLibrary liferayLibrary;

	public void execute() throws Exception {
		Iterator i = getFileItems().iterator();
		while (i.hasNext()) {
			FileItem fi = (FileItem) i.next();
			String fileName = fi.getName();
			if (!Is.emptyString(fileName)) {
				updateFileEntry(fileName, fi.getInputStream(), fi.getSize());
			}
		}
	}

	public String[] getNextControllers() throws Exception {
		return PREVIOUS_CONTROLLERS;
	}

	public String getCustomView() throws Exception {
		return PREVIOUS_VIEW;
	}

	public void setFileItems(List fileItems) {
		this.fileItems = fileItems;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public List getFileItems() {
		return fileItems;
	}

	protected void updateFileEntry(String sourceFileName, InputStream is,
			long size) throws Exception {

		// Get the Liferay userId 
		// The user must be logged in and needs "add document" and "create folder" permissions
		// for the root folder
		
		String user = Users.getCurrent();
		if (user == null) {
			addError("liferay_document_editor_requires_user"); 
			return;
		}
		
		long userId;
		try {	
			userId = Long.parseLong(user);  
		}
		catch (NumberFormatException ex) {
			addError("user_must_be_numeric"); 
			return;			
		}
		
		//Tested in Liferay 5.2.3
		ServiceContext serviceContext = new ServiceContext();

		//Check if the folder exists,  
		//If the folder doesn't exist, create it under root folder
		ClassLoader cl = PortalClassLoaderUtil.getClassLoader();
		DynamicQuery dqi = DynamicQueryFactoryUtil.forClass(DLFolder.class,
				cl);
		Criterion crit = PropertyFactoryUtil.forName("name").eq(
				liferayLibrary.getOid());
		dqi.add(crit);
		java.util.List<Object> results = DLFolderLocalServiceUtil
				.dynamicQuery(dqi);
		boolean folderExists;
		if (results.size() > 0){
			folderExists = true;
			liferayLibrary.setFolder((DLFolder)results.get(0));}
		else
			folderExists = false;
		if (!folderExists) {
			//Create folder and associate it to the library			
			if (liferayLibrary.getRootFolder() == null) {
				addError("liferay_library_root_folder_not_found");
				return;
			}			
			liferayLibrary.setFolder(DLFolderLocalServiceUtil.addFolder(liferayLibrary.getOid(), userId,
					liferayLibrary.getRootFolder().getGroupId(), liferayLibrary
							.getRootFolder().getFolderId(), liferayLibrary
							.getOid(), "Documents from the library oid="
							+ liferayLibrary.getOid(), serviceContext));
		}

		UUIDCalculator cal = new UUIDCalculator();
		String uuid = (String) cal.calculate();

		DLFileEntry entry = DLFileEntryLocalServiceUtil.addFileEntry(uuid,
				userId, liferayLibrary.getFolder().getFolderId(), sourceFileName,
				sourceFileName, "", "", is, size, serviceContext);

		liferayLibrary.addDocument(entry.getUuid(), entry.getTitle(), entry.getName());
	}



}
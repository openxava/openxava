package org.openxava.session;

import java.util.*;

import org.openxava.hibernate.*;
import com.liferay.portal.*;
import com.liferay.portal.kernel.dao.orm.*;
import com.liferay.portal.kernel.util.*;
import com.liferay.portlet.documentlibrary.model.*;
import com.liferay.portlet.documentlibrary.service.*;

/**
 * 
 * @author Oscar Caro 
 */
public class LiferayLibrary {
	private Map documents = null;
	private String oid;
	private DLFolder rootFolder;
	private DLFolder folder;
	private boolean alsoDeleteFile;
	
	
	public void loadAllDocuments(LiferayLibrary library) throws SystemException {
		long folderId=0;
		if (documents != null) documents.clear();
		//Tested in Liferay 5.2.3
		ClassLoader cl = PortalClassLoaderUtil.getClassLoader();
		DynamicQuery dqi = DynamicQueryFactoryUtil.forClass(DLFolder.class, cl);
		Criterion crit = PropertyFactoryUtil.forName("name").eq(library.getOid());
		dqi.add(crit);
		
		java.util.List<Object> results=DLFolderLocalServiceUtil.dynamicQuery(dqi);
		if (results.size()>0) {
			folder=(DLFolder)results.get(0);
			folderId=folder.getFolderId();
		}

		List ficheros = DLFolderLocalServiceUtil.getFileEntriesAndFileShortcuts(folderId, 0, 20);
		
		for (Iterator it = ficheros.iterator(); it.hasNext(); ) {
			DLFileEntry dlFileEntry=(DLFileEntry)it.next();
			LiferayDocument libraryDocument=new LiferayDocument();
			libraryDocument.setDlFileEntryId(dlFileEntry.getUuid());
			libraryDocument.setName(dlFileEntry.getName());
			libraryDocument.setTitle(dlFileEntry.getTitle());
			addDocument(libraryDocument);
		}
	}

	public void addDocument(String dlFileEntryId,String title, String name) {
		LiferayDocument libraryDocument = new LiferayDocument();
		libraryDocument.setLibraryId(oid);
		libraryDocument.setDlFileEntryId(dlFileEntryId);
		libraryDocument.setTitle(title);
		libraryDocument.setName(name);
		
		addDocument(libraryDocument);
	}

	
	private void addDocument(LiferayDocument libraryDocument) {
		if (documents == null) documents = new HashMap();
		documents.put(libraryDocument.getDlFileEntryId(), libraryDocument);
	}
	
	public void removeDocument(String uuid) {
		documents.remove(uuid);
	}

	public Collection getDocuments() {
		return documents == null?Collections.EMPTY_LIST:documents.values();
	}
	

	public LiferayDocument getDocument(String uuid) {
		return (LiferayDocument) XHibernate.getSession().get(LiferayDocument.class, uuid);
	}

	public DLFileEntry getLRDocument(String uuid) throws PortalException, SystemException {
		LiferayDocument document=(LiferayDocument)documents.get(uuid);
		return DLFileEntryLocalServiceUtil.getFileEntry(folder.getFolderId(), document.getName());
	}
	
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public boolean isEmpty() {
		if (documents == null) return true;
		return documents.isEmpty();
	}

	
	public DLFolder getRootFolder() {
		return rootFolder;
	}

	public void setRootFolder(DLFolder rootFolder) {
		this.rootFolder = rootFolder;
	}

	public DLFolder getFolder() {
		return folder;
	}

	public void setFolder(DLFolder folder) {
		this.folder = folder;
	}

	public boolean isAlsoDeleteFile() {
		return alsoDeleteFile;
	}

	public void setAlsoDeleteFile(boolean alsoDeleteFile) {
		this.alsoDeleteFile = alsoDeleteFile;
	}

	public void setDocuments(Map documents) {
		this.documents = documents;
	}

	
}

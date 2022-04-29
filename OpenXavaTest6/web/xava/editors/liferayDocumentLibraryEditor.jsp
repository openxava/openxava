<%@ include file="../imports.jsp"%>

<%@ page import="org.openxava.model.meta.MetaProperty"%>
<%@ page import="org.openxava.web.Ids"%>
<%@ page import="org.openxava.calculators.UUIDCalculator"%>
<%@ page import="org.openxava.util.Is"%>
<%@ page import="java.util.*"%>
<%@ page import="org.openxava.session.*"%>
<%@ page import="com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil"%>
<%@ page import="com.liferay.portlet.documentlibrary.model.DLFolder"%>
<%@ page import="com.liferay.portal.kernel.dao.orm.*"%>
<%@ page import="com.liferay.portal.kernel.util.PortalClassLoaderUtil"%>
<%@ page import="org.openxava.view.View"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext"
	scope="session" />
<%
	String propertyKey = request.getParameter("propertyKey");
	MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
	boolean editable = "true".equals(request.getParameter("editable"));
	boolean alsoDeleteFile = "true".equals(request.getParameter("alsoDeleteFile"));
	//Get root folder name
	String rootFolderNameOrId = request.getParameter("rootFolderNameOrId");
	String folderCalculator = request.getParameter("folder_calculator");
	
	//We can use folderId or folder name in editors.xml to set the root folderEl 
	//If we use folder name, this name should be unique in the document library
	DLFolder rootFolder=null;
	//First, try to get the root folder assuming rootFolderNameOrId is the id
	try{
		long rootFolderId=Long.parseLong(rootFolderNameOrId);
		rootFolder=DLFolderLocalServiceUtil.getDLFolder(Long.parseLong(rootFolderNameOrId));
	}
	catch (Exception ex){
		//There isn't any folder with folderId= {rootFolderNameOrId}
	}

	if (rootFolder==null){
		//Get the root folder by name
		//We assume that the folder name is unique
		ClassLoader cl = PortalClassLoaderUtil.getClassLoader();
		DynamicQuery dqi = DynamicQueryFactoryUtil.forClass(DLFolder.class, cl);
		Criterion crit = PropertyFactoryUtil.forName("name").eq(rootFolderNameOrId);
		dqi.add(crit);	
		java.util.List<Object> results=DLFolderLocalServiceUtil.dynamicQuery(dqi);
		if (results.size()>0) rootFolder=(DLFolder)results.get(0);
	}	
	String fvalue = (String) request.getAttribute(propertyKey
	+ ".fvalue");
	//Create or retrieve Library object from the context
	//from this application, module and object
	org.openxava.session.LiferayLibrary library = (org.openxava.session.LiferayLibrary) context
	.get(request, "xava_liferayLibrary");
	if (Is.emptyString(fvalue)) {
		UUIDCalculator cal = new UUIDCalculator();
		fvalue = (String) cal.calculate();
	}
	library.setOid(fvalue);
	library.setRootFolder(rootFolder);
	library.setAlsoDeleteFile(alsoDeleteFile);
	//Load current document list
	library.loadAllDocuments(library);
%>

<input type="hidden" name="<%=propertyKey%>" id="<%=propertyKey%>" value="<%=fvalue%>">

<%
	if (editable) {
%>

<span valign='middle'> <%
 	for (Iterator it = library.getDocuments().iterator(); it
 				.hasNext();) {
 			LiferayDocument libraryDocument = (LiferayDocument) it
 					.next();
 			libraryDocument.getTitle();
 %>
 

 <a href="/c/document_library/get_file?uuid=<%=libraryDocument.getDlFileEntryId()%>&groupId=<%=library.getFolder().getGroupId()%>" target="_blank" > <%=libraryDocument.getTitle()%></a> 
 <xava:image action='LiferayDocumentLibrary.deleteDocument'
	argv='<%="libraryDocumentFileEntryId="
		+ Ids.undecorate(libraryDocument.getDlFileEntryId()) %>'
		/>			 
<%
	} //for
%>
</span>

<span valign='middle'> 
	<xava:image action='LiferayDocumentLibrary.addDocument' />	
&nbsp;&nbsp; 
</span>
<%
	} //if
%>
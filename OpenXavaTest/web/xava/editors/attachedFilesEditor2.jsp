<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.util.Is" %>
<%-- tmp <%@ page import="org.openxava.session.Gallery"%> --%>
<%-- tmp ini --%>
<%@ page import="java.util.Collection" %>
<%@ page import="org.openxava.web.editors.FilePersistorFactory" %>
<%@ page import="org.openxava.web.editors.AttachedFile" %>
<%-- tmp fin --%>  

<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String applicationName = request.getParameter("application");
String module = request.getParameter("module");
boolean editable = "true".equals(request.getParameter("editable"));
String dataEditable = editable?"":"data-editable='true'";
StringBuilder imagesOids = new StringBuilder(); // tmp Not this name
Object value = request.getAttribute(propertyKey + ".value");
if (!Is.empty(value)) {
	/* tmp
	Gallery gallery = Gallery.find((String) value);
	for (String imageOid: gallery.getImagesOids()) {
		if (imagesOids.length() > 0) imagesOids.append(',');
		imagesOids.append(imageOid);
	}
	*/
	// tmp ini
	Collection<AttachedFile> files = FilePersistorFactory.getInstance().findLibrary((String) value);
	for (AttachedFile file : files) {
		if (imagesOids.length() > 0) imagesOids.append(',');
		imagesOids.append(file.getId());
	}
	// tmp fin	
}
String dataEmpty = imagesOids.length() == 0?"data-empty='true'":"";
System.out.println("[attachedFilesEditor2.jsp] imagesOids=" + imagesOids); // tmp 
%>
<input id='<%=propertyKey%>' 
	type="file" class="xava_upload ox-files" <%-- tmp Mover a Style. ¿Este nombre? --%>  
	data-multiple="true"
	data-application="<%=applicationName%>" 
	data-module="<%=module%>"
	data-files="<%=imagesOids%>" 
	<%=dataEmpty%>
	<%=dataEditable%>/>
	
<input type="hidden" name="<%=propertyKey%>" value="<%=value%>">	

<jsp:include page="filePondTranslation.jsp"/>	

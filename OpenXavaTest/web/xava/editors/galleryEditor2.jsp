<%@ include file="../imports.jsp"%>

<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.util.Is" %>
<%@ page import="org.openxava.session.Gallery"%> <%-- tmp --%>
<%@ page import="org.openxava.session.GalleryImage"%> <%-- tmp --%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/> <%-- tmp --%>

<%-- tmp
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
String viewObject = request.getParameter("viewObject");
String viewObjectArgv = Is.emptyString(viewObject) || "xava_view".equals(viewObject)?"":(",viewObject=" + viewObject);
String editAction = request.getParameter("editAction"); // tmp ¿Que hacemos con esto?
if (Is.emptyString(editAction)) editAction = "Gallery.edit"; 
--%>

<%
// tmp ¿Se podría fusionar con imageEditor.jsp?
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String applicationName = request.getParameter("application");
String module = request.getParameter("module");
/* tmp
Object value = request.getAttribute(propertyKey + ".value");
String dataEmpty = Is.empty(value)?"data-empty='true'":""; 
*/
boolean editable = "true".equals(request.getParameter("editable"));
String dataEditable = editable?"":"data-editable='true'";
// tmp ini
StringBuilder imagesOids = new StringBuilder();
Object value = request.getAttribute(propertyKey + ".value");
if (!Is.empty(value)) {
	Gallery gallery = Gallery.find((String) value);
	for (String imageOid: gallery.getImagesOids()) {
		if (imagesOids.length() > 0) imagesOids.append(',');
		imagesOids.append(imageOid);
	}
}
String dataEmpty = imagesOids.length() == 0?"data-empty='true'":""; 
// tmp fin
%>


<input type="hidden" name="<%=propertyKey%>" value="<%=value%>"> <%-- tmp ¿Necesario? --%>

<input id='<%=propertyKey%>' 
	type="file" class="xava_gallery" 
	data-application="<%=applicationName%>" 
	data-module="<%=module%>"
	data-images="<%=imagesOids%>" <%-- tmp ¿Este nombre? --%>
	<%=dataEmpty%>
	<%=dataEditable%>/>

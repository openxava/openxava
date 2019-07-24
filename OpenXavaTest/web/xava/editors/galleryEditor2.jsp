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
Object value = request.getAttribute(propertyKey + ".value");
String dataEmpty = Is.empty(value)?"data-empty='true'":""; // tmp ¿Aplica?
boolean editable = "true".equals(request.getParameter("editable"));
String dataEditable = editable?"":"data-editable='true'";
// tmp ini
// tmp ME QUEDÉ POR AQUÍ: HE DE COGER EL LA gallery DEL context EN VEZ DE CREAR UNA NUEVA. POR SEGURIDAD Y PARA QUE FUNCIONE EL SERVLET DE LEER LA IMAGEN. ¿ASÍ PODRÍA PONER MÁS DE UNA GALERÍA POR ENTIDAD?
Gallery gallery = new Gallery();
gallery.setOid((String) value);
gallery.loadAllImages();
StringBuilder imagesOids = new StringBuilder();
for (GalleryImage image: gallery.getImages()) {
	if (imagesOids.length() > 0) imagesOids.append(',');
	imagesOids.append(image.getOid());
}
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

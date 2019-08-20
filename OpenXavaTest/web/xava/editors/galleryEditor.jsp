<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.util.Is" %>
<%@ page import="org.openxava.session.Gallery"%> 

<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String applicationName = request.getParameter("application");
String module = request.getParameter("module");
boolean editable = "true".equals(request.getParameter("editable"));
String dataEditable = editable?"":"data-editable='true'";
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
%>

<input id='<%=propertyKey%>' 
	type="file" class="xava_upload"  
	data-multiple="true"
	data-application="<%=applicationName%>" 
	data-module="<%=module%>"
	data-files="<%=imagesOids%>" 
	<%=dataEmpty%>
	<%=dataEditable%>/>
	
<input type="hidden" name="<%=propertyKey%>" value="<%=value%>">	

<jsp:include page="filePondTranslation.jsp"/>	

<%-- tmp --%>

<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.util.Is" %>
<%-- tmp <%@ page import="org.openxava.session.Gallery"%> --%>
<%-- tmp ini --%>
<%@ page import="org.openxava.util.XavaException" %>
<%@ page import="org.openxava.web.editors.IUploadFilesIdsProvider" %>
<%-- tmp fin --%>  

<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String applicationName = request.getParameter("application");
String module = request.getParameter("module");
boolean editable = "true".equals(request.getParameter("editable"));
String dataEditable = editable?"":"data-editable='true'";
// tmp StringBuilder imagesOids = new StringBuilder(); 
Object value = request.getAttribute(propertyKey + ".value");
// tmp ini
String dataFiles = "";
String filesIds = null; 
if (!Is.empty(value)) {
	String filesIdsProviderClass = request.getParameter("filesIdsProviderClass");
	if (Is.emptyString(filesIdsProviderClass)) {
		throw new XavaException("filesIdsProviderClass parameter missed for uploadEditor.jsp"); // tmp i18n
	}
	IUploadFilesIdsProvider filesIdsProvider = (IUploadFilesIdsProvider) Class.forName(filesIdsProviderClass).newInstance(); 
	filesIds = filesIdsProvider.getFilesIds(value);
	if (filesIds != null) {
		dataFiles = "data-files='" + filesIds + "'"; 
	}
}
// tmp fin
// tmp String dataEmpty = imagesOids.length() == 0?"data-empty='true'":""; 
// tmp ini
System.out.println("[uploadEditor.jsp] 'null'.equals(value)=" + "null".equals(value));
String dataEmpty = "null".equals(value) || Is.empty(value) || !Is.empty(value) && filesIds != null && "".equals(filesIds)?"data-empty='true'":""; 
String cssClass = request.getParameter("cssClass");
cssClass = Is.emptyString(cssClass)?"":" " + cssClass;
boolean multiple = "true".equals(request.getParameter("multipleFiles"));
String dataMultiple = multiple?"data-multiple='true'":"";
boolean preview = !"false".equals(request.getParameter("imagePreview"));
String dataPreview = !preview?"data-preview='false'":"";
// tmp fin
%>
<input id='<%=propertyKey%>' 
	type="file" class="xava_upload<%=cssClass%>"
	data-application="<%=applicationName%>" 
	data-module="<%=module%>"
	<%=dataMultiple%>
	<%=dataPreview%>
	<%=dataFiles%> 
	<%=dataEmpty%>
	<%=dataEditable%>/>
	
<input type="hidden" name="<%=propertyKey%>" value="<%=value%>">

<jsp:include page="filePondTranslation.jsp"/>	

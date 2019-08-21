<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.web.Ids" %>
<%@ page import="org.openxava.util.Is"%>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String applicationName = request.getParameter("application");
String module = request.getParameter("module");
Object value = request.getAttribute(propertyKey + ".value");
String dataEmpty = Is.empty(value)?"data-empty='true'":"";
boolean editable = "true".equals(request.getParameter("editable"));
String dataEditable = editable?"":"data-editable='true'";
%>

<input id='<%=propertyKey%>' 
	type="file" class="xava_upload <%=style.getImage()%>"  
	data-application="<%=applicationName%>" 
	data-module="<%=module%>"
	<%=dataEmpty%>
	<%=dataEditable%>/> 

<jsp:include page="filePondTranslation.jsp"/>
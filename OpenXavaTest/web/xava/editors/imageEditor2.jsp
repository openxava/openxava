<%@ include file="../imports.jsp"%>

<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.web.Ids" %>
<%@ page import="org.openxava.util.Is"%>

<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
boolean editable="true".equals(request.getParameter("editable"));
String applicationName = request.getParameter("application");
String module = request.getParameter("module");
Object value = request.getAttribute(propertyKey + ".value");
String dataEmpty = Is.empty(value)?"data-empty='true'":"";
%>

<div class="ox-image">
<input type="file" class="xava_image" <%-- tmp Comprobar que lo de xava_image está bien, ver otros casos --%> 
	data-application="<%=applicationName%>" 
	data-module="<%=module%>"
	data-property="<%=Ids.undecorate(propertyKey)%>" 
	<%=dataEmpty%>/> 
</div>	


<%@ include file="../imports.jsp"%>

<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.web.Ids" %>
<%@page import="org.openxava.util.Is"%>

<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
boolean editable="true".equals(request.getParameter("editable"));
String applicationName = request.getParameter("application");
String module = request.getParameter("module");
long dif=System.currentTimeMillis(); // to avoid browser caching
Object value = request.getAttribute(propertyKey + ".value");
String url = Is.empty(value)?"":request.getContextPath() + "/xava/ximage?application=" + applicationName + "&module=" + module + "&property=" + propertyKey + "&dif=" + dif;
%>

<input type="file" class="xava_image" <%-- tmp Comprobar que lo de xava_image está bien, ver otros casos --%> 
	data-application="<%=applicationName%>" 
	data-module="<%=module%>" 
	data-url="<%=url%>"/> 


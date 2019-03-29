<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.view.View" %>
<%@ page import="org.openxava.tab.Tab" %>
<%@ page import="org.openxava.web.WebEditors" %>


<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<%
String viewObject = request.getParameter("viewObject");
viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject;
View view = (View) context.get(request, viewObject);
String propertyKey = request.getParameter("propertyKey");
String url = (String) view.getObject("xava.myReportColumnDescriptionsListEditorURL");
url = url.replace("${propertyKey}", propertyKey);
%>	

<jsp:include page="<%=url%>"/>
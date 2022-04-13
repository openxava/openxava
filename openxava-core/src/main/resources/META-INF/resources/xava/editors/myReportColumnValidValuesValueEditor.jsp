<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.view.View" %>
<%@ page import="org.openxava.tab.Tab" %>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<%
String viewObject = request.getParameter("viewObject");
viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject;
View view = (View) context.get(request, viewObject);
String tabObject = request.getParameter("tabObject");
tabObject = (tabObject == null || tabObject.equals(""))?"xava_tab":tabObject;
Tab tab = (Tab) context.get(request, tabObject);
String propertyName = view.getValueString("name");
MetaProperty property = tab.getMetaTab().getMetaModel().getMetaProperty(propertyName);
String propertyKey = request.getParameter("propertyKey");
request.setAttribute(propertyKey + ".validValuesProperty", property); 
%>	

<jsp:include page="validValuesEditor.jsp"/>


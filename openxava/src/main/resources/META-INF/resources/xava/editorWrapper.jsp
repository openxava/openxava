<%@ include file="imports.jsp"%>
<%@ page import="org.openxava.util.Is" %>
<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<%
String propertyName = request.getParameter("propertyName");
org.openxava.view.View view = (org.openxava.view.View) context.get(request, request.getParameter("viewObject"));
String propertyStyle = view.getStyle(propertyName);
%>
<% if (!Is.empty(propertyStyle)) { %><span class="<%=propertyStyle%>"><% } %>
<xava:editor 
	property='<%=propertyName%>' 
	editable='<%=Boolean.valueOf(request.getParameter("editable")).booleanValue()%>' 
	throwPropertyChanged='<%=Boolean.valueOf(request.getParameter("throwPropertyChanged")).booleanValue()%>'/>
<% if (!Is.empty(propertyStyle)) { %></span><% } %>


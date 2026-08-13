<%@ include file="imports.jsp"%>
<%@page import="org.openxava.web.servlets.Servlets"%>
<%@page import="org.openxava.web.render.Parts"%>
<% Servlets.setCharacterEncoding(request, response); %>
<%= Parts.render(request, response, "detail.jsp") %>

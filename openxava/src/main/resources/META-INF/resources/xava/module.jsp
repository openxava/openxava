<%@ include file="imports.jsp"%>
<%@page import="org.openxava.web.servlets.Servlets"%>
<%@page import="org.openxava.web.render.ModulePageRenderer"%>
<% Servlets.setCharacterEncoding(request, response); %>
<%= ModulePageRenderer.render(request, response) %>

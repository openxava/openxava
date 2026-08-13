<%@page import="org.openxava.web.render.Parts"%>
<% String _detailURL = "detail?viewObject=" + request.getParameter("viewObject")
	+ "&propertyPrefix=" + request.getParameter("propertyPrefix")
	+ "&first=" + request.getParameter("first"); %>
<%= Parts.render(request, response, _detailURL) %>

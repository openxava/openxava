<%
if ("true".equals(request.getParameter("fixedModules"))) {
	modulesList = modules.getFixedModules(request); 
}
else if ("true".equals(request.getParameter("bookmarkModules"))) {
	modulesList = modules.getBookmarkModules(request); 
	bookmarkModules = true;
}
else {
	modulesList = modules.getRegularModules(request); 
}
%>
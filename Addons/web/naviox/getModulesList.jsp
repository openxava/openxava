<%
if ("true".equals(request.getParameter("fixedModules"))) {
	modulesList = modules.getFixedModules();
}
else if ("true".equals(request.getParameter("bookmarkModules"))) {
	modulesList = modules.getBookmarkModules();
	bookmarkModules = true;
}
else {
	modulesList = modules.getRegularModules();
}
%>
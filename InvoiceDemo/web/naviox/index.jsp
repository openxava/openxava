<%Servlets.setCharacterEncoding(request, response);%>

<%@include file="../xava/imports.jsp"%>

<%@page import="org.openxava.web.servlets.Servlets"%>
<%@page import="org.openxava.util.Locales"%>
<%@page import="org.openxava.util.XavaPreferences"%>
<%@page import="org.openxava.web.style.XavaStyle"%>
<%@page import="org.openxava.web.style.Themes"%> 

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="modules" class="com.openxava.naviox.Modules" scope="session"/>

<%
String windowId = context.getWindowId(request);
context.setCurrentWindowId(windowId);
if ("true".equals(request.getParameter("init"))) {
	context.resetModule(request);
}
String app = request.getParameter("application");
String module = context.getCurrentModule(request);
modules.setCurrent(request); 
String oxVersion = org.openxava.controller.ModuleManager.getVersion();
String title = (String) request.getAttribute("naviox.pageTitle");
if (title == null) title = modules.getCurrentModuleDescription(request); 
boolean hasModules = modules.hasModules(request); 		
org.openxava.controller.ModuleManager manager = (org.openxava.controller.ModuleManager) context
		.get(app, module, "manager", "org.openxava.controller.ModuleManager");
manager.setSession(session);
manager.setApplicationName(request.getParameter("application"));
manager.setModuleName(module); // In order to show the correct description in head 
%>

<!DOCTYPE html>

<head>
	<title><%=title%></title>
	<link href="<%=request.getContextPath()%>/xava/style/layout.css?ox=<%=oxVersion%>" rel="stylesheet" type="text/css">
    <link href="<%=request.getContextPath()%>/xava/style/<%=Themes.getCSS(request)%>?ox=<%=oxVersion%>" rel="stylesheet" type="text/css"> 
	<link rel="stylesheet" href="<%=request.getContextPath()%>/xava/style/materialdesignicons.css?ox=<%=oxVersion%>">
	<script type='text/javascript' src='<%=request.getContextPath()%>/xava/js/dwr-engine.js?ox=<%=oxVersion%>'></script>
	<script type='text/javascript' src='<%=request.getContextPath()%>/dwr/interface/Modules.js?ox=<%=oxVersion%>'></script>
	<script type='text/javascript' src='<%=request.getContextPath()%>/dwr/interface/Folders.js?ox=<%=oxVersion%>'></script>
</head>

<body <%=XavaStyle.getBodyClass(request)%>>
	
	<div id="main"> 
				
		<% if (hasModules) { %>
			<jsp:include page="leftMenu.jsp"/>
		<% } %>
		
		<div class="module-wrapper">
			<div id="module_header">
				 <jsp:include page="moduleHeader.jsp"/>
			</div>
			<% if ("SignIn".equals(module)) {  %>
			<jsp:include page='signIn.jsp'/>
			<% } else { %>
			<div id="module"> 	
				<jsp:include page='<%="../xava/module.jsp?application=" + app + "&module=" + module + "&htmlHead=false"%>'/>
			</div> 
			<% } %>
		</div>
		
	</div> 
	
	<%@include file="indexExt.jsp"%>

	<script type='text/javascript' src='<%=request.getContextPath()%>/naviox/js/naviox.js?ox=<%=oxVersion%>'></script> 
	
	<script>
	$(function() {
		naviox.lockSessionMilliseconds = <%=com.openxava.naviox.model.Configuration.getInstance().getLockSessionMilliseconds()%>; 
		naviox.application = "<%=app%>";
		naviox.module = "<%=module%>";
		naviox.locked = <%=context.get(request, "naviox_locked")%>;
		naviox.init();
	});
	</script>
	

</body>
</html>

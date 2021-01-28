<%Servlets.setCharacterEncoding(request, response);%>

<%@include file="../xava/imports.jsp"%>

<%@page import="org.openxava.web.servlets.Servlets"%>
<%@page import="org.openxava.util.Locales"%>
<%@page import="org.openxava.util.XavaPreferences"%>
<%@page import="org.openxava.web.style.XavaStyle"%>
<%@page import="org.openxava.web.style.Themes"%> 
<%@page import="com.openxava.naviox.util.Organizations"%>
<%@page import="org.openxava.util.Users"%>
<%@page import="com.openxava.naviox.util.NaviOXPreferences"%>
<%@page import="org.openxava.util.Is"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="modules" class="com.openxava.naviox.Modules" scope="session"/>

<%
if ("true".equals(request.getParameter("init"))) {
	context.resetModule(request);
}
String windowId = context.getWindowId(request);
context.setCurrentWindowId(windowId);
String app = request.getParameter("application");
String module = context.getCurrentModule(request);
Locales.setCurrent(request);
modules.setCurrent(request.getParameter("application"), request.getParameter("module"));
String oxVersion = org.openxava.controller.ModuleManager.getVersion();
String title = (String) request.getAttribute("naviox.pageTitle");
if (title == null) title = modules.getCurrentModuleDescription(request); 
boolean hasModules = modules.hasModules(request); 		
org.openxava.controller.ModuleManager manager = (org.openxava.controller.ModuleManager) context
		.get(app, module, "manager", "org.openxava.controller.ModuleManager");
manager.setSession(session);
manager.setApplicationName(request.getParameter("application"));
manager.setModuleName(module); // In order to show the correct description in head 
boolean isFirstSteps = com.openxava.naviox.Modules.FIRST_STEPS.equals(module);
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
				<% if (!isFirstSteps) { %>
				<a id="module_header_menu_button" href="javascript:naviox.showModulesList('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>')">
					<i class="mdi mdi-menu"></i></a>
				<% } %>	
				<span id="module_title">
					<%
					if (hasModules && !isFirstSteps) {
					%>
					<span id="module_extended_title">
						<%
						String organizationName = modules.getOrganizationName(request);
						if (!Is.emptyString(organizationName)) {
						%> 
						<%=organizationName%> - 
						<%
						}
						%>						
						<%=modules.getApplicationLabel(request)%> -
					</span>	 
					<%
					}
					%>
					
					<%String moduleTitle = hasModules?modules.getCurrentModuleLabel():modules.getCurrentModuleDescription(request);%>
					<%=moduleTitle%>
				</span>	
				<a href="javascript:naviox.bookmark()" title="<xava:message key='<%=modules.isCurrentBookmarked(request)?"unbookmark_module":"bookmark_module"%>'/>"> 
					<i id="bookmark" class='mdi mdi-star<%=modules.isCurrentBookmarked(request)?"":"-outline"%>'></i> 
				</a>
				<div id="sign_in_out">
					<%
					if (Is.emptyString(NaviOXPreferences.getInstance().getAutologinUser())) {
						String userName = Users.getCurrent();
						String currentModule = request.getParameter("module");
						boolean showSignIn = userName == null && !currentModule.equals("SignIn");						
						if (showSignIn) {
							String selected = "SignIn".equals(currentModule)?"selected":"";
					%>
					<a href="<%=request.getContextPath()%>/m/SignIn" class="sign-in <%=selected%>">
							<xava:message key="signin"/>
					</a>
					<%
						}
						if (userName != null) {
							String organization = Organizations.getCurrent(request);
							if (organization == null) organization = "";
					%>
					<a  href="<%=request.getContextPath()%>/naviox/signOut.jsp?organization=<%=organization%>" class="sign-in"><xava:message key="signout"/> (<%=userName%>)</a>
					<%
						}
					} 
					%>
				</div>
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

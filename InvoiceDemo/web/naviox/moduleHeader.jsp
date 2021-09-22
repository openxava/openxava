<%@include file="../xava/imports.jsp"%>

<%@page import="org.openxava.application.meta.MetaModule"%> 
<%@page import="org.openxava.util.Is"%>
<%@page import="com.openxava.naviox.util.NaviOXPreferences"%>
<%@page import="org.openxava.util.Users"%>
<%@page import="com.openxava.naviox.util.Organizations"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="modules" class="com.openxava.naviox.Modules" scope="session"/>

<%
String module = context.getCurrentModule(request);
boolean hasModules = modules.hasModules(request);
boolean isFirstSteps = com.openxava.naviox.Modules.FIRST_STEPS.equals(module);
%>

<div id="module_header_left"> 			
	<% if (!isFirstSteps) { %>
		<a id="module_header_menu_button" href="javascript:naviox.showModulesList('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>')">
			<i class="mdi mdi-menu"></i></a>
	<% } %>				
	<%
	String organizationName = modules.getOrganizationName(request);
	if (!Is.emptyString(organizationName)) organizationName += " - ";
	String title = organizationName + modules.getApplicationLabel(request);
	%>
	<% if (hasModules && !isFirstSteps) { %>
		<span id="module_extended_title">
			<%=title%> :
		</span>	 
	<%
	}
	else if ("SignIn".equals(module)) {
	%>
		<%=title%>
	<%
	}

	for (MetaModule metaModule: modules.getTopModules()) {
		if (metaModule.getName().equals("SignIn")) continue;
		boolean isSelected = metaModule.getName().equals(request.getParameter("module")); 
		String selected = isSelected?"selected":"unselected";
		if (isSelected) {
	%>		
		<span class="selected"><%=metaModule.getLabel(request.getLocale())%></span>
	<%
		}
		else {
	%>
		<a href="<%=modules.getModuleURI(request, metaModule)%>?retainOrder=true" class="unselected"><%=metaModule.getLabel(request.getLocale())%></a>
	<%
		}	
	}
	%>				
	&nbsp;
</div>
				
<div id="module_header_right">
	<a id="bookmark" href="javascript:naviox.bookmark()" title="<xava:message key='<%=modules.isCurrentBookmarked(request)?"unbookmark_module":"bookmark_module"%>'/>"> 
		<i class='mdi mdi-star<%=modules.isCurrentBookmarked(request)?"":"-outline"%>'></i> 
	</a> 				
	<span id="sign_in_out"> 
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
	</span>				
</div>

	
<%@page import="com.openxava.naviox.util.OrganizationsCurrent"%> 
<%@page import="org.openxava.web.Browsers"%>
<%@page import="org.openxava.util.Users"%>

<jsp:useBean id="modules" class="com.openxava.naviox.Modules" scope="session"/>

<%
if (Users.getCurrent() != null || OrganizationsCurrent.get(request) != null) {
	System.out.println("[index.jsp] A. Users.getCurrent()=" + Users.getCurrent()); // tmr
	System.out.println("[index.jsp] A. OrganizationsCurrent.get(request)=" + OrganizationsCurrent.get(request)); // tmr
	String module = Users.getCurrent() == null?"SignIn":modules.getCurrent(request);
	System.out.println("[index.jsp] A. module=" + module); // tmr
	String url = Browsers.isMobile(request) && !"Index".equals(modules.getCurrent(request))?"phone":"m/" + module;
	System.out.println("[index.jsp] A. url=" + url); // tmr
	// tmr ini
	if (Users.getCurrent() == null && session.getAttribute("naviox.originalURL") == null) {
		session.setAttribute("naviox.originalURL", request.getContextPath());
		url = request.getContextPath() + "/azure/signIn";
		System.out.println("[index.jsp] A. url.2=" + url); // tmr
	}
	// tmr fin
%>

<script type="text/javascript">
window.location="<%=url%>";
</script>

<%
}
else {
// tmr ini
	if (session.getAttribute("naviox.originalURL") == null) {
		session.setAttribute("naviox.originalURL", request.getContextPath()); 
		%>
		<script type="text/javascript">
		window.location="<%=request.getContextPath()%>/azure/signIn";
		</script>
		<% 
	}
	else {
		%>
		<jsp:include page="naviox/welcome.jsp"/>
		<%	
	}
}
%>

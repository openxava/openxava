<%@include file="../xava/imports.jsp"%> 

<%@page import="com.openxava.naviox.util.OrganizationsCurrent"%> 
<%@page import="org.openxava.web.Browsers"%>
<%@page import="org.openxava.util.Users"%>
<%@page import="com.openxava.naviox.impl.SignInHelper"%> 

<jsp:useBean id="modules" class="com.openxava.naviox.Modules" scope="session"/>

<%
String signInURL = SignInHelper.getSignInURL(); 
if (Users.getCurrent() != null || OrganizationsCurrent.get(request) != null) {
	String module = Users.getCurrent() == null?"SignIn":modules.getCurrent(request);
	String url = Browsers.isMobile(request) && !"Index".equals(modules.getCurrent(request))?"phone":"m/" + module;
	if (signInURL != null && Users.getCurrent() == null && session.getAttribute("naviox.originalURL") == null) {
		session.setAttribute("naviox.originalURL", request.getContextPath());
		url = request.getContextPath() + signInURL;
	}
%>

<script type="text/javascript" <xava:nonce/>>
window.location="<%=url%>";
</script>

<%
}
else {
	if (signInURL != null && session.getAttribute("naviox.originalURL") == null) {
		session.setAttribute("naviox.originalURL", request.getContextPath());
		System.out.println("[index.jsp] jsp:forward"); // tmr 
		%>
		<jsp:forward page="<%=signInURL%>"/>
		<%-- tmr
		<script type="text/javascript" <xava:nonce/>>
		window.location="<%=request.getContextPath()%><%=signInURL%>";
		</script>
		--%>
		<% 
	}
	else {
		%>
		<jsp:include page="naviox/welcome.jsp"/>
		<%	
	}
}
%>

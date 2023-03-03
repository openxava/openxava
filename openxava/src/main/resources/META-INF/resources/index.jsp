<%@page import="com.openxava.naviox.util.OrganizationsCurrent"%> 
<%@page import="org.openxava.web.Browsers"%>
<%@page import="org.openxava.util.Users"%>

<jsp:useBean id="modules" class="com.openxava.naviox.Modules" scope="session"/>

<%
if (Users.getCurrent() != null || OrganizationsCurrent.get(request) != null) {
	String module = Users.getCurrent() == null?"SignIn":modules.getCurrent(request);
	String url = Browsers.isMobile(request) && !"Index".equals(modules.getCurrent(request))?"phone":"m/" + module;
%>

<script type="text/javascript" nonce="tmr1"> <%-- tmr nonce ¿En migration para código propio? --%>
window.location="<%=url%>";
</script>

<%
}
else {
%>
<jsp:include page="naviox/welcome.jsp"/>
<%
}
%>

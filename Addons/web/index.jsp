<%@page import="com.openxava.naviox.util.Organizations"%> 
<%@page import="org.openxava.web.Browsers"%>
<%@page import="org.openxava.util.Users"%>

<jsp:useBean id="modules" class="com.openxava.naviox.Modules" scope="session"/>

<%
if (Users.getCurrent() != null || Organizations.getCurrent(request) != null) {
	String module = Users.getCurrent() == null?"SignIn":modules.getCurrent(request);
	String url = Browsers.isMobile(request) && !"Index".equals(modules.getCurrent(request))?"phone":"m/" + module;
%>

<script type="text/javascript">
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

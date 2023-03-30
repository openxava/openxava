<%@page import="com.openxava.naviox.util.OrganizationsCurrent"%> 
<%@page import="org.openxava.web.Browsers"%>
<%@page import="org.openxava.util.Users"%>

<jsp:useBean id="modules" class="com.openxava.naviox.Modules" scope="session"/>

<%
if (Users.getCurrent() != null || OrganizationsCurrent.get(request) != null) {
	String module = Users.getCurrent() == null?"SignIn":modules.getCurrent(request);
	String url = Browsers.isMobile(request) && !"Index".equals(modules.getCurrent(request))?"phone":"m/" + module;
%>

<script type="text/javascript">
window.location="<%=url%>";
</script>

<%
}
else {
// tmr ini
// TMR ME QUEDÉ POR AQUÍ: ESTO FUNCIONA PARA ROOT, FALTA PROBARLO MEJOR (CON 2 APPS) Y PROBAR LO DEL MODULO OTRA VEZ
// TRM   TENDRIA QUE QUITAR LO DE DEFAULT
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

<%@page import="org.openxava.controller.ModuleManager"%>
<%@page import="com.openxava.naviox.model.Configuration"%>
<%@page import="com.openxava.naviox.util.NaviOXPreferences"%>

<%
StringBuilder base = new StringBuilder("..");
if (!Configuration.getInstance().isSharedUsersBetweenOrganizations() 
		&&
	!NaviOXPreferences.getInstance().isShowOrganizationOnSignIn()
		&&
	!"".equals(request.getParameter("organization")))
{
	base.append("/o/").append(request.getParameter("organization"));
}
session.invalidate();
%>

<script type="text/javascript">
window.location="<%=String.format("%s/m/SignIn", base)%>";
</script>

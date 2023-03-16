<%--
NaviOX. Navigation and security for OpenXava applications.
Copyright 2014 Javier Paniza Lucas

License: Apache License, Version 2.0.	
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

License: GNU Lesser General Public License (LGPL), version 2.1 or later.
See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
--%>

<%@include file="../xava/imports.jsp"%> <%-- tmr --%>

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

<script type="text/javascript" <xava:nonce/>> <%-- tmr nonce --%>
window.location="<%=String.format("%s/m/SignIn", base)%>";
</script>

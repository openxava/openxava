<%Servlets.setCharacterEncoding(request, response);%>

<%--
NaviOX. Navigation and security for OpenXava applications.
Copyright 2014 Javier Paniza Lucas

License: Apache License, Version 2.0.	
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

License: GNU Lesser General Public License (LGPL), version 2.1 or later.
See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
--%>

<%@include file="../xava/imports.jsp"%>

<%@page import="org.openxava.web.servlets.Servlets"%>
<%@page import="org.openxava.application.meta.MetaApplications"%>
<%@page import="org.openxava.application.meta.MetaApplication"%>
<%@page import="org.openxava.util.Locales"%>
<%@page import="org.openxava.web.style.XavaStyle"%>
<%@page import="org.openxava.util.XavaPreferences"%>
<%@page import="org.openxava.web.Browsers"%> 

<%-- To put your own text add entries in the i18n messages files of your project 
In MyApplication-labels_en.properties:
MyApplication=My application
MyApplication[description]=My application does this and that

In MyApplication-messages_en.properties:
welcome_point1=This is a additional explanatory line
--%>

<%
MetaApplication metaApplication = MetaApplications.getMainMetaApplication(); 
Locales.setCurrent(request);
String oxVersion = org.openxava.controller.ModuleManager.getVersion();
String title = (String) request.getAttribute("naviox.pageTitle");
if (title == null) title = metaApplication.getLabel();
%>

<!DOCTYPE html>

<head>
	<title><%=title%></title>
	<meta name='viewport' content='width=device-width, initial-scale=1, maximum-scale=1'>
	<link href="<%=request.getContextPath()%>/xava/style/<%=XavaPreferences.getInstance().getStyleCSS()%>?ox=<%=oxVersion%>" rel="stylesheet" type="text/css">
</head>

<body id="welcome" <%=XavaStyle.getBodyClass(request)%>>

<h1><%=metaApplication.getLabel()%></h1>
<p><%=metaApplication.getDescription()%></p>
<p><xava:message key="welcome_point1"/></p> 
<p id="signin_tip"><xava:message key="signin_tip"/></p> 

<div class="ox-bottom-buttons">
	<input type="hidden">
	<%-- tmr
	<input type="button" tabindex="1" onclick="window.location='m/SignIn'" value="<xava:label key='SignIn'/>">
	%>
	<%-- tmr ini --%>
	<%-- tmr En migración poner dos opciones: nonce y href --%>
	<a href="m/SignIn">
	<input type="button" tabindex="1" value="<xava:label key='SignIn'/>">
	</a>
	<%-- tmr fin --%>   
</div>

</body>

<%-- tmr nonce ¿En migration para código propio?  --%>
<%-- tmr ini 
<script type="text/javascript" <xava:nonce/>> 
	var button = document.getElementById('welcome_go_signin');
	button.onclick = function () { window.location='m/SignIn'; }
</script>
--%>
<%-- tmr fin --%>

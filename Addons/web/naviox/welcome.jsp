<%@include file="../xava/imports.jsp"%>

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
	<% if (Browsers.isIE(request)) { %>
	<script type='text/javascript' src="<%=request.getContextPath()%>/xava/js/css-vars-ponyfill.js?ox=<%=oxVersion%>"></script>
	<script type='text/javascript'>cssVars({ }); </script>	
	<% } %>
	
</head>

<body id="welcome" <%=XavaStyle.getBodyClass(request)%>>

<h1><%=metaApplication.getLabel()%></h1>
<p><%=metaApplication.getDescription()%></p>
<p><xava:message key="welcome_point1"/></p> 
<p id="signin_tip"><xava:message key="signin_tip"/></p> 

<div class="ox-bottom-buttons">
	<input type="hidden">
	<input type="button" tabindex="1" onclick="window.location='m/SignIn'" value="<xava:label key='SignIn'/>">   
</div>

</body>


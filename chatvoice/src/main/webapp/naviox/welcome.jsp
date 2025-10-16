<%Servlets.setCharacterEncoding(request, response);%>

<%-- Welcome page. Feel free to modify at your taste --%>

<%@include file="../xava/imports.jsp"%>

<%@page import="org.openxava.web.servlets.Servlets"%>
<%@page import="org.openxava.application.meta.MetaApplications"%>
<%@page import="org.openxava.application.meta.MetaApplication"%>
<%@page import="org.openxava.util.Locales"%>
<%@page import="org.openxava.web.style.XavaStyle"%>
<%@page import="org.openxava.util.XavaPreferences"%>
<%@page import="org.openxava.web.Browsers"%> 

<%-- To put your own text add entries in the i18n messages files of your project 
In chatvoice-labels_en.properties:
chatvoice=My application
chatvoice[description]=My application does this and that

In chatvoice-messages_en.properties:
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
	<a href="m/SignIn">
	<input type="button" tabindex="1" value="<xava:label key='SignIn'/>">
	</a>
</div>

</body>

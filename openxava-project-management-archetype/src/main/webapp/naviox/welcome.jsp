<%@include file="../xava/imports.jsp"%>

<%@page import="org.openxava.application.meta.MetaApplications"%>
<%@page import="org.openxava.application.meta.MetaApplication"%>
<%@page import="org.openxava.util.Locales"%>
<%@page import="org.openxava.web.style.XavaStyle"%>
<%@page import="org.openxava.util.XavaPreferences"%>
<%@page import="org.openxava.util.XavaResources"%>

<%
// TMR FALTA REFINAR
String applicationName = request.getContextPath().substring(1);
MetaApplication metaApplication = MetaApplications.getMetaApplication(applicationName);
Locales.setCurrent(request);
String oxVersion = org.openxava.controller.ModuleManager.getVersion();
String language = "es".equals(Locales.getCurrent().getLanguage()) || "ca".equals(Locales.getCurrent().getLanguage())?"es":"en";
String welcomePoint1 = (String) request.getAttribute("description1");
if (welcomePoint1 == null) welcomePoint1 = XavaResources.getString(request, "welcome_point1");
String welcomePoint2 = (String) request.getAttribute("description2");
if (welcomePoint2 == null) welcomePoint2 = XavaResources.getString(request, "welcome_point2");
String title = (String) request.getAttribute("pageTitle");
if (title == null) title =  "XavaProjects: " + XavaResources.getString(request, "page_title");
else title = title + " - XavaProjects";
%>

<!DOCTYPE html>

<head>
	<title><%=title%></title>
	<meta name='viewport' content='width=device-width, initial-scale=1, maximum-scale=1'>
	<link href="<%=request.getContextPath()%>/xava/style/<%=XavaPreferences.getInstance().getStyleCSS()%>?ox=<%=oxVersion%>" rel="stylesheet" type="text/css">
	<link href="<%=request.getContextPath()%>/xava/style/custom.css?ox=<%=oxVersion%>" rel="stylesheet" type="text/css">
</head>

<body id="welcome" <%=XavaStyle.getBodyClass(request)%>>


<div id="logo">
<div><%=metaApplication.getLabel()%></div>
</div>

<p><%=welcomePoint1%></p> 
<p><%=welcomePoint2%></p> 

<div class="ox-bottom-buttons">
	<input type="hidden">
	<a href="m/SignIn">
	<input type="button" tabindex="1" value="<xava:label key='SignIn'/>">
	</a>
</div>

<div class="group">

	<% for (int i=1; i<=4; i++) { %>
		<div class='block'>
			<%
			String featureHeader = XavaResources.getString(request, "feature_header" + i);
			String featureDescription = XavaResources.getString(request, "feature_description" + i);
			%>
			<h4><%=featureHeader%></h4>
			<p><%=featureDescription%></p>			
		</div>
	<% } %>

</div>

</body>


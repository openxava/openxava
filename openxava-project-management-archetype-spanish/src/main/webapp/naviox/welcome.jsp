<%@include file="../xava/imports.jsp"%>

<%@page import="org.openxava.application.meta.MetaApplications"%>
<%@page import="org.openxava.application.meta.MetaApplication"%>
<%@page import="org.openxava.util.Locales"%>
<%@page import="org.openxava.web.style.XavaStyle"%>
<%@page import="org.openxava.util.XavaPreferences"%>
<%@page import="org.openxava.util.XavaResources"%>

<%
MetaApplication metaApplication = MetaApplications.getMainMetaApplication(); 
Locales.setCurrent(request);
String oxVersion = org.openxava.controller.ModuleManager.getVersion();
String title = (String) request.getAttribute("naviox.pageTitle");
if (title == null) title = metaApplication.getLabel();
String welcomePoint1 = XavaResources.getString(request, "welcome_point1");
String welcomePoint2 = XavaResources.getString(request, "welcome_point2");
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

<div class="grupo">

	<% for (int i=1; i<=5; i++) { %>
		<div class='bloque'>
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

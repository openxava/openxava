<%@include file="../xava/imports.jsp"%>

<%@page import="com.openxava.naviox.util.Organizations"%>

<%-- To put your own text add entries in the i18n messages files of your project --%>

<%
String organization = Organizations.getCurrent(request);
%>

<div id="user_join">
	<xava:message key="user_join_question" param="<%=organization%>"/>
</div>


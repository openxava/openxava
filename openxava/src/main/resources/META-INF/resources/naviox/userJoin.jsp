<%--
NaviOX. Navigation and security for OpenXava applications.
Copyright 2014 Javier Paniza Lucas

License: Apache License, Version 2.0.	
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

License: GNU Lesser General Public License (LGPL), version 2.1 or later.
See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
--%>

<%@include file="../xava/imports.jsp"%>

<%@page import="com.openxava.naviox.util.Organizations"%>

<%-- To put your own text add entries in the i18n messages files of your project --%>

<%
String organization = OrganizationsCurrent.get(request);
%>

<div id="user_join">
	<xava:message key="user_join_question" param="<%=organization%>"/>
</div>


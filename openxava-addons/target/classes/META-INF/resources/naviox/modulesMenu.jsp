<%@page import="com.openxava.naviox.util.NaviOXPreferences"%>

<%@include file="../xava/imports.jsp"%>

<div id="modules_list_core">
<jsp:include page="<%=NaviOXPreferences.getInstance().getModulesListJSP()%>"/>
</div>

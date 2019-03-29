<%@ include file="../imports.jsp"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<%
String tabObject = request.getParameter("tabObject");
tabObject = (tabObject == null || tabObject.equals(""))?"xava_tab":tabObject;
org.openxava.tab.Tab tab = (org.openxava.tab.Tab) context.get(request, "xava_customizingTab");
String applicationName = request.getParameter("application");
String module = request.getParameter("module");
%>
<input type="hidden" id="xava_application" value="<%=applicationName%>" /> 
<input type="hidden" id="xava_module" value="<%=module%>" /> 
<div id="xava_search_columns">
<input id="xava_search_columns_text" type="text" size="38" placeholder='<xava:message key="search_columns"/>'/>
</div>
<div id="xava_add_columns">
<jsp:include page="selectColumns.jsp"/>
</div>
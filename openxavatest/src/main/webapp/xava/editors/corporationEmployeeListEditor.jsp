<%@ include file="../imports.jsp"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<%
String tabObject = request.getParameter("tabObject");
tabObject = (tabObject == null || tabObject.equals(""))?"xava_tab":tabObject;
org.openxava.tab.Tab tab = (org.openxava.tab.Tab) context.get(request, tabObject);
String condition = tab.getBaseCondition()==null?"":tab.getBaseCondition();
String all = condition.equals("")?"selected":"";
String low = condition.contains("<=")?"selected":"";
String high = condition.contains(">")?"selected":"";
%>
<%-- 
Using data-application and data-module instead of openxava.lastApplication and openxava.lastModule in JavaScrip
allows using the several modules in the same HTML page.
--%>
<select name="<xava:id name='chooseSegment'/>" class='corporation-employee-list-select' 
	data-application="<%=request.getParameter("application")%>"
	data-module="<%=request.getParameter("module")%>">
	<option value="all" <%=all%>>All employees</option>
	<option value="low" <%=low%>>Low salary employees</option>
	<option value="high" <%=high%>>High salary employees</option>
</select>

<jsp:include page="listEditor.jsp"/>
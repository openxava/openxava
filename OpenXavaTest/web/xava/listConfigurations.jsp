<%@ include file="imports.jsp"%> 

<%@ page import="org.openxava.util.Strings" %>
<%@ page import="org.openxava.tab.Tab"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<%
String tabObject = request.getParameter("tabObject"); 
tabObject = (tabObject == null || tabObject.equals(""))?"xava_tab":tabObject;
Tab tab = (Tab) context.get(request, tabObject);
tab.setRequest(request);
String confName = tab.getConfigurationName();
%>

<select name='<xava:id name="listConfigurations"/>' title="<%=confName%>"  
	onchange="openxava.executeAction('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>', '', false, 'List.filter','configurationId=' + this.value)">
	
	<option value=""><%=confName%></option>
	<% 
	int count = 1; 
	for (Tab.Configuration conf: tab.getConfigurations()) {
		if (!confName.equals(conf.getName())) {
			if (++count > Tab.MAX_CONFIGURATIONS_COUNT) break; 
	%>
	<option value="<%=conf.getId()%>"><%=conf.getName()%></option>
	<% 
		}
	} 
	%>
</select>
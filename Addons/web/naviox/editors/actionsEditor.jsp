<%@ include file="../../xava/imports.jsp"%>

<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.view.View" %>
<%@ page import="org.openxava.application.meta.MetaApplications" %>
<%@ page import="org.openxava.application.meta.MetaModule" %>
<%@ page import="org.openxava.controller.meta.MetaControllers" %>
<%@ page import="org.openxava.controller.meta.MetaController" %>
<%@ page import="org.openxava.controller.meta.MetaAction" %>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>
<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<%@ include file="commonDefinitions.jsp" %> 

<table width="100%"><tr>
<%
java.util.Collection actions = java.util.Arrays.asList(fvalues); 
int i=0; 
for (Object ocontroller: module.getControllersNames()) {
	MetaController controller = MetaControllers.getMetaController((String) ocontroller);
	for (Object oaction: controller.getAllNotHiddenMetaActionsRecursive()) { 
		MetaAction action = (MetaAction) oaction;
		if (action.getMetaController().getName().equals("Navigation")) continue;
		String checked = actions.contains(action.getQualifiedName())?"checked='true'":"";
%>
	<td>
	<INPUT name="<%=propertyKey%>" type="checkbox" class="<%=style.getEditor()%>" 
		tabindex="1" 
		value="<%=action.getQualifiedName()%>" 
		<%=checked%>
		<%=disabled%>
		<%=script%>
	/>		
	<%=action.getLabel()%> 
	<% if (++i % 4 == 0) { %></tr><tr><% } %>
	</td>
<%		
	}
}
%>
<%@ include file="collectionActions.jsp" %>
</tr></table>
<% 
if (!editable) { 
	for (i=0; i<fvalues.length; i++) {
%>
		<input type="hidden" name="<%=propertyKey%>" value="<%=fvalues[i]%>">		
<%
	}
} 
%>	

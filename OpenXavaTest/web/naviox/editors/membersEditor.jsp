<%@ include file="../../xava/imports.jsp"%>

<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.model.meta.MetaModel" %>
<%@ page import="org.openxava.model.meta.MetaMember" %>
<%@ page import="org.openxava.view.View" %>
<%@ page import="org.openxava.application.meta.MetaApplications" %>
<%@ page import="org.openxava.controller.meta.MetaControllers" %>
<%@ page import="org.openxava.application.meta.MetaModule"%>
<%@ page import="org.openxava.controller.meta.MetaAction" %>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>
<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"><%@page import="org.openxava.controller.meta.MetaController"%></jsp:useBean>

<%@ include file="commonDefinitions.jsp" %> 

<input type="hidden" name="<%=propertyKey%>" value="<%=module.getModelName()%>">
<table width="100%"><tr>
<%
java.util.Collection members = java.util.Arrays.asList(fvalues); 
int i=0; 
boolean excludeReadOnly = "true".equalsIgnoreCase(request.getParameter("excludeReadOnly")); 
MetaModel model = MetaModel.get(module.getModelName());
for (String memberName: model.getMembersNames()) {
	MetaMember member = model.getMetaMember(memberName);
	if (member.isHidden()) continue;
	if (excludeReadOnly && member instanceof MetaProperty && ((MetaProperty) member).isReadOnly()) continue;
	String checked = members.contains(memberName)?"checked='true'":"";		
%>
	<td>
	<INPUT name="<%=propertyKey%>" type="checkbox" class="<%=style.getEditor()%>" 
		tabindex="1" 
		value="<%=memberName%>" 
		<%=checked%>
		<%=disabled%>
		<%=script%>
	/>		
	<%=member.getLabel()%> 
	<% if (++i % 5 == 0) { %></tr><tr><% } %>
	</td>
<%		
}
%>
</tr></table>
<% 
if (!editable) { 
	for (i=1; i<fvalues.length; i++) {
%>
		<input type="hidden" name="<%=propertyKey%>" value="<%=fvalues[i]%>">		
<%
	}
} 
%>	

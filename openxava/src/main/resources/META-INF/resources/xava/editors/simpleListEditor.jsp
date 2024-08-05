<%-- tmr --%>

<%@ include file="../imports.jsp"%>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.openxava.util.Maps" %>
<%@ page import="org.openxava.view.View" %>
<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.web.WebEditors" %>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>

<%
String collectionName = request.getParameter("collectionName");
String viewObject = request.getParameter("viewObject");
View view = (View) context.get(request, viewObject);
View subview = view.getSubview(collectionName);
%>

<table id="<xava:id name='<%=collectionName%>'/>" class="ox-simple-list">

<tr>
<%
// Heading
for (MetaProperty p: subview.getMetaPropertiesList()) {
	String label = p.getQualifiedLabel(request);
%>
	<th><%=label%></th>
<%
	}
%>
</tr>

<%
// Values
List<Map<String,Object>> values = subview.getCollectionValues();
for (Map row: values) {
%>
<tr>
<%
	for (MetaProperty p: subview.getMetaPropertiesList()) { 
		String align =p.isNumber() && !p.hasValidValues()?"ox-text-align-right":"";
		String fvalue = null;
		Object value = null;
		String propertyName = p.getName();
		value = Maps.getValueFromQualifiedName(row, propertyName);
		fvalue = WebEditors.format(request, p, value, errors, view.getViewName(), true);	
		Object title = WebEditors.formatTitle(request, p, value, errors, view.getViewName(), true);
%>
	<td class="<%=align%>">
	<div title="<%=title%>" class="<xava:id name='tipable'/>"><%=fvalue%></div>
	</td>
<%
	}
%>	
</tr>
<%
}
%>
</table>
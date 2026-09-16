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

<%
List<MetaProperty> properties = subview.getMetaPropertiesList();
List<Map<String,Object>> values = subview.getCollectionValues();

// Max absolute value per numeric column, for the inline bar
double[] maxes = new double[properties.size()];
for (Map row: values) {
	int i = 0;
	for (MetaProperty p: properties) {
		if (p.isNumber() && !p.hasValidValues()) {
			Object v = Maps.getValueFromQualifiedName(row, p.getName());
			if (v instanceof Number) {
				maxes[i] = Math.max(maxes[i], Math.abs(((Number) v).doubleValue()));
			}
		}
		i++;
	}
}
%>

<table id="<xava:id name='<%=collectionName%>'/>" class="ox-simple-list">

<tr>
<%
// Heading
for (MetaProperty p: properties) {
	String label = p.getQualifiedLabel(request);
	String headerAlign = p.isNumber() && !p.hasValidValues()?"ox-text-align-right":"";
%>
	<th class="<%=headerAlign%>"><%=label%></th>
<%
	}
%>
</tr>

<%
// Values
for (Map row: values) {
%>
<tr>
<%
	int i = 0;
	for (MetaProperty p: properties) { 
		boolean numeric = p.isNumber() && !p.hasValidValues();
		String align = numeric?"ox-text-align-right":"";
		String fvalue = null;
		Object value = null;
		String propertyName = p.getName();
		value = Maps.getValueFromQualifiedName(row, propertyName);
		fvalue = WebEditors.format(request, p, value, errors, view.getViewName(), true);	
		Object title = WebEditors.formatTitle(request, p, value, errors, view.getViewName(), true);
		String barStyle = "";
		if (numeric && maxes[i] > 0 && value instanceof Number) {
			int percent = (int) Math.round(Math.abs(((Number) value).doubleValue()) / maxes[i] * 100);
			barStyle = " style='background: linear-gradient(to left, var(--simple-list-bar-color) " + percent + "%, transparent " + percent + "%);'";
		}
		i++;
%>
	<td class="<%=align%>"<%=barStyle%>>
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
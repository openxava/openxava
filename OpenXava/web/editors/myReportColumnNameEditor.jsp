<%@ include file="../imports.jsp"%>

<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.actions.OnChangeMyReportColumnNameAction" %>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<% 
boolean editable="true".equals(request.getParameter("editable"));
if (!editable) {
%>

<jsp:include page="textEditor.jsp"/>

<% } else { %>

<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
String script = request.getParameter("script");

String viewObject = request.getParameter("viewObject");
viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject;
org.openxava.view.View view = (org.openxava.view.View) context.get(request, viewObject);
Boolean oshowAllColumns = (Boolean) view.getObject("xava.myReportColumnShowAllColumns");
boolean showAllColumns = oshowAllColumns==null?false:oshowAllColumns.booleanValue();

String tabObject = request.getParameter("tabObject");
tabObject = (tabObject == null || tabObject.equals(""))?"xava_tab":tabObject;
org.openxava.tab.Tab tab = (org.openxava.tab.Tab) context.get(request, tabObject);
java.util.Collection columns = showAllColumns?
	tab.getMetaTab().getMetaModel().getRecursiveQualifiedPropertiesNames():
	tab.getMetaTab().getMetaModel().getRecursiveQualifiedPropertiesNamesUntilSecondLevel();
showAllColumns = tab.getMetaTab().getMetaModel().getRecursiveQualifiedPropertiesNames().size() == columns.size(); 	
if (!showAllColumns && 
	!org.openxava.util.Is.emptyString(fvalue) && 
	!OnChangeMyReportColumnNameAction.SHOW_MORE.equals(fvalue) && 
	!columns.contains(fvalue)) 
{
	columns = new java.util.ArrayList(columns);
	columns.add(fvalue);
}
%>

<select id="<%=propertyKey%>" name="<%=propertyKey%>" tabindex="1" class=<%=style.getEditor()%> <%=script%> title="<%=p.getDescription(request)%>">
	<option value=""></option>
	<% 
	for (java.util.Iterator it = columns.iterator(); it.hasNext(); ) {
		String column = (String) it.next();
		MetaProperty property = tab.getMetaTab().getMetaModel().getMetaProperty(column);
		property = property.cloneMetaProperty();
		property.setQualifiedName(column);
		String label = property.getQualifiedLabel(org.openxava.util.Locales.getCurrent());		
		String selected = column.equals(fvalue)?"selected":""; 
	%>
	<option value="<%=column%>" <%=selected%>><%=label%></option>
	<% 
	}
	%>
	<% if (!showAllColumns) {%>
	<option value="<%=OnChangeMyReportColumnNameAction.SHOW_MORE%>"><xava:message key="my_report_show_more_columns"/></option>
	<% } %>
</select>	

<% } %>
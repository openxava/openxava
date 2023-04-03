<%@ include file="imports.jsp"%>

<%@page import="org.openxava.controller.meta.MetaAction"%>
<%@page import="org.openxava.web.Ids"%>
<%@page import="org.openxava.controller.meta.MetaControllers"%>
<%@page import="org.openxava.util.Is"%>
<%@page import="org.openxava.web.Actions"%>
<%@page import="org.openxava.util.XavaPreferences"%>

<%
String tabObject = request.getParameter("tabObject"); 
tabObject = (tabObject == null || tabObject.equals(""))?"xava_tab":tabObject;
String onSelectCollectionElementAction = subview.getOnSelectCollectionElementAction();
String rowStyle = "border-bottom: 1px solid;";
MetaAction onSelectCollectionElementMetaAction = Is.empty(onSelectCollectionElementAction) ? null : MetaControllers.getMetaAction(onSelectCollectionElementAction);
boolean resizeColumns = style.allowsResizeColumns() && XavaPreferences.getInstance().isResizeColumns();
boolean sortable = subview.isCollectionSortable();
%>
<% if (resizeColumns) { %> 
<div class="<xava:id name='collection_scroll'/> ox-overflow-auto">
<% } %>
<table id="<xava:id name='<%=idCollection%>'/>" class="ox-list" <%=style.getListCellSpacing()%>>
<% if (sortable) { %><tbody class="xava_sortable_row"><% } %> 
<tr class="ox-list-header">
	<%
		if (lineAction != null) {
	%>	
	<th class="ox-list-header"></th>
	<%
		}
	%>	
	<th class="ox-list-header" width="5">
	<%
		String actionOnClickAll = Actions.getActionOnClickAll(
		request.getParameter("application"), request.getParameter("module"), 
		onSelectCollectionElementAction, idCollection, propertyPrefix, 
		"", rowStyle, tabObject);
	%>
	<input type="checkbox" name="<xava:id name='xava_selected_all'/>" value="<%=propertyPrefix%>selected_all" <%=actionOnClickAll%> />
	</th>
<%
	// Heading
Iterator it = subview.getMetaPropertiesList().iterator();
for (int columnIndex=0; it.hasNext(); columnIndex++) {
	MetaProperty p = (MetaProperty) it.next();
	String label = p.getQualifiedLabel(request);
	int columnWidth = subview.getCollectionColumnWidth(columnIndex);
	String width = columnWidth<0 || !resizeColumns?"":"width: " + columnWidth + "px";
%>
	<th class="ox-list-header" style="padding-right: 0px">
		<div id="<xava:id name='<%=idCollection%>'/>_col<%=columnIndex%>" class="<%=((resizeColumns)?("xava_resizable"):(""))%>" style="<%=width%>" >
		<%if (resizeColumns) {%><nobr><%}%>
		<%=label%>&nbsp;
		<%if (resizeColumns) {%></nobr><%}%>
		</div>
	</th>
<%
	}
%>
</tr>

<%
	// Values
Collection aggregates = subview.getCollectionValues();
if (aggregates == null) aggregates = java.util.Collections.EMPTY_LIST;
Iterator itAggregates = aggregates.iterator();
for (int f=0; itAggregates.hasNext(); f++) {
	Map row = (Map) itAggregates.next();
	String cssClass=f%2==0?"ox-list-pair":"ox-list-odd";
	String cssCellClass=f%2==0?"ox-list-pair":"ox-list-odd";
	String selectedClass = "";
	if (f == subview.getCollectionEditingRow()) { 
		selectedClass = f%2==0?style.getListPairSelected():style.getListOddSelected();
		cssClass = cssClass + " " + selectedClass;		
		if (style.isApplySelectedStyleToCellInList()) cssCellClass = cssCellClass + " " + selectedClass; 
	}		
	String idRow = Ids.decorate(request, propertyPrefix) + f;	
	String events=f%2==0?style.getListPairEvents():style.getListOddEvents(); 
%>
<tr id="<%=idRow%>" class="<%=cssClass%>" <%=events%>>
<%
	if (lineAction != null) {
%>
<td class="<%=cssCellClass%> ox-list-action-cell">
<nobr>
	<%if (sortable) { %>
	<i class="xava_handle mdi mdi-swap-vertical"></i>	
	<%}%>	
<xava:action action="<%=lineAction%>" argv='<%="row="+f + ",viewObject="+viewName%>'/>
<% 
	if (style.isSeveralActionsPerRow())
	for (java.util.Iterator itRowActions = subview.getRowActionsNames().iterator(); itRowActions.hasNext(); ) { 	
		String rowAction = (String) itRowActions.next();		
%>
<xava:action action='<%=rowAction%>' argv='<%="row=" + f + ",viewObject="+viewName%>'/>
<%
	}
%>
</nobr>
</td>
<%
	} 
	String actionOnClick = Actions.getActionOnClick(
		request.getParameter("application"), request.getParameter("module"), 
		onSelectCollectionElementAction, f, viewName, idRow,
		"", rowStyle, 
		onSelectCollectionElementMetaAction, tabObject);
%>
<td class="<%=cssCellClass%>" width="5">
<input type="checkbox" name="<xava:id name='xava_selected'/>" value="<%=propertyPrefix%>__SELECTED__:<%=f%>" <%=actionOnClick%>/>
</td>
<%
	it = subview.getMetaPropertiesList().iterator();	
	for (int columnIndex = 0; it.hasNext(); columnIndex++) { 
		MetaProperty p = (MetaProperty) it.next();
		String align =p.isNumber() && !p.hasValidValues()?"vertical-align: middle;text-align: right; ":"vertical-align: middle; ";
		String cellStyle = align;
		int columnWidth = subview.getCollectionColumnWidth(columnIndex);
		String width = columnWidth<0 || !resizeColumns?"":"width: " + columnWidth + "px"; 
		String fvalue = null;
		Object value = null;
		String propertyName = p.getName();
		value = Maps.getValueFromQualifiedName(row, propertyName);
		fvalue = WebEditors.format(request, p, value, errors, view.getViewName(), true);	
		Object title = WebEditors.formatTitle(request, p, value, errors, view.getViewName(), true); 
%>
	<td class="<%=cssCellClass%>" style="<%=cellStyle%>; padding-right: 0px">
	<xava:link action="<%=lineAction%>" argv='<%="row="+f + ",viewObject="+viewName%>' cssStyle="text-decoration: none; outline: none">
	<div title="<%=title%>" class="<xava:id name='tipable'/> <xava:id name='<%=idCollection%>'/>_col<%=columnIndex%>" style="overflow: hidden; <%=width%>">
	<%if (resizeColumns) {%><nobr><%}%>
	<%=fvalue%>&nbsp; 
	<%if (resizeColumns) {%></nobr><%}%>
	</div>
	</xava:link>
	</td>
		
<%
	}
}
%>
</tr>
<jsp:include page="collectionTotals.jsp" />
<% if (sortable) { %></tbody><% } %>
</table>
<% if (resizeColumns) { %>
</div>
<% } %>
 
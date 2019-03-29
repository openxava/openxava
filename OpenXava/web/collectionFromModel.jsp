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
String selectedRowStyle = style.getSelectedRowStyle();
String rowStyle = "border-bottom: 1px solid;";
MetaAction onSelectCollectionElementMetaAction = Is.empty(onSelectCollectionElementAction) ? null : MetaControllers.getMetaAction(onSelectCollectionElementAction);
boolean resizeColumns = style.allowsResizeColumns() && XavaPreferences.getInstance().isResizeColumns();
String browser = request.getHeader("user-agent");
boolean scrollSupported = !(browser != null && (browser.indexOf("MSIE 6") >= 0 || browser.indexOf("MSIE 7") >= 0));
String styleOverflow = org.openxava.web.Lists.getOverflow(browser, subview.getMetaPropertiesList());
boolean sortable = subview.isCollectionSortable();
%>
<% if (resizeColumns && scrollSupported) { %> 
<div class="<xava:id name='collection_scroll'/>" style="<%=styleOverflow%>">
<% } %>
<table id="<xava:id name='<%=idCollection%>'/>" class="<%=style.getList()%>" <%=style.getListCellSpacing()%> style="<%=style.getListStyle()%>">
<% if (sortable) { %><tbody class="xava_sortable_row"><% } %> 
<tr class="<%=style.getListHeader()%>">
	<%
		if (lineAction != null) {
	%>	
	<th class=<%=style.getListHeaderCell()%>></th>
	<%
		}
	%>	
	<th class=<%=style.getListHeaderCell()%> width="5">
	<%
		String actionOnClickAll = Actions.getActionOnClickAll(
		request.getParameter("application"), request.getParameter("module"), 
		onSelectCollectionElementAction, idCollection, propertyPrefix, 
		selectedRowStyle, rowStyle, tabObject);
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
	<th class=<%=style.getListHeaderCell()%> style="padding-right: 0px">
		<div id="<xava:id name='<%=idCollection%>'/>_col<%=columnIndex%>" class="<%=((resizeColumns)?("xava_resizable"):(""))%>" style="overflow: hidden; <%=width%>" >
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
	String cssClass=f%2==0?style.getListPair():style.getListOdd();
	String cssCellClass=f%2==0?style.getListPairCell():style.getListOddCell();
	String selectedClass = "";
	if (f == subview.getCollectionEditingRow()) { 
		selectedClass = f%2==0?style.getListPairSelected():style.getListOddSelected();
		cssClass = cssClass + " " + selectedClass;		
		if (style.isApplySelectedStyleToCellInList()) cssCellClass = cssCellClass + " " + selectedClass; 
	}		
	String idRow = Ids.decorate(request, propertyPrefix) + f;	
	String events=f%2==0?style.getListPairEvents():style.getListOddEvents(); 
%>
<tr id="<%=idRow%>" class="<%=cssClass%>" <%=events%> style="border-bottom: 1px solid;">
<%
	if (lineAction != null) {
%>
<td class="<%=cssCellClass%>" style="vertical-align: middle;text-align: center;padding-right: 2px; <%=style.getListCellStyle()%>">
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
		selectedRowStyle, rowStyle, 
		onSelectCollectionElementMetaAction, tabObject);
%>
<td class="<%=cssCellClass%>" width="5" style="<%=style.getListCellStyle()%>">
<input type="checkbox" name="<xava:id name='xava_selected'/>" value="<%=propertyPrefix%>__SELECTED__:<%=f%>" <%=actionOnClick%>/>
</td>
<%
	it = subview.getMetaPropertiesList().iterator();	
	for (int columnIndex = 0; it.hasNext(); columnIndex++) { 
		MetaProperty p = (MetaProperty) it.next();
		String align =p.isNumber() && !p.hasValidValues()?"vertical-align: middle;text-align: right; ":"vertical-align: middle; ";
		String cellStyle = align + style.getListCellStyle();
		int columnWidth = subview.getCollectionColumnWidth(columnIndex);
		String width = columnWidth<0 || !resizeColumns?"":"width: " + columnWidth + "px"; 
		String fvalue = null;
		Object value = null;
		String propertyName = p.getName();
		value = Maps.getValueFromQualifiedName(row, propertyName);		
		if (p.hasValidValues()) {
			if (value instanceof Number) {
				fvalue = p.getValidValueLabel(request, ((Number) value).intValue());
			}
			else {
				// In this case value is a enum type
				fvalue = p.getValidValueLabel(request, value);
			}
		}
		else {
			fvalue = WebEditors.format(request, p, value, errors, view.getViewName(), true);	
		}
		Object title = WebEditors.formatTitle(request, p, value, errors, view.getViewName(), true); 
%>
	<td class="<%=cssCellClass%>" style="<%=cellStyle%>; padding-right: 0px">
	<% if (style.isRowLinkable()) { %> 	
	<xava:link action="<%=lineAction%>" argv='<%="row="+f + ",viewObject="+viewName%>' cssStyle="text-decoration: none; outline: none">
	<div title="<%=title%>" class="<xava:id name='tipable'/> <xava:id name='<%=idCollection%>'/>_col<%=columnIndex%>" style="overflow: hidden; <%=width%>">
	<%if (resizeColumns) {%><nobr><%}%>
	<%=fvalue%>&nbsp; 
	<%if (resizeColumns) {%></nobr><%}%>
	</div>
	</xava:link>
	<% } else { %>	 
	<div title="<%=title%>" class="<xava:id name='tipable'/> <xava:id name='<%=idCollection%>'/>_col<%=columnIndex%>" style="overflow: hidden; <%=width%>">
		<%if (resizeColumns) {%><nobr><%}%>
	 	<%=fvalue%>&nbsp;
	 	<%if (resizeColumns) {%></nobr><%}%>	 
	</div>
	<% } %> 
	</td>
		
<%
	}
}
%>
</tr>
<jsp:include page="collectionTotals.jsp" />
<% if (sortable) { %></tbody><% } %>
</table>
<% if (resizeColumns && scrollSupported) { %>
</div>
<% } %>
 
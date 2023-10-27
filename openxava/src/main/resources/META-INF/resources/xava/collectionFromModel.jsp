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
		/* tmr
		String actionOnClickAll = Actions.getActionOnClickAll(
		request.getParameter("application"), request.getParameter("module"), 
		onSelectCollectionElementAction, idCollection, propertyPrefix, 
		"", "", tabObject);
		*/
	%>
	<%-- tmr
	<input type="checkbox" name="<xava:id name='xava_selected_all'/>" value="<%=propertyPrefix%>selected_all" <%=actionOnClickAll%> />
	--%>
	<%-- tmr ini --%>
	<input type="checkbox" name="<xava:id name='xava_selected_all'/>" value="<%=propertyPrefix%>selected_all" 
		data-on-select-collection-element-action="<%=onSelectCollectionElementAction%>"
		data-view-object="<%=idCollection%>"
		data-prefix="<%=propertyPrefix%>"
		data-tab-object="<%=tabObject%>"/>
	<%-- tmr fin --%>
	</th>
<%
	// Heading
Iterator it = subview.getMetaPropertiesList().iterator();
for (int columnIndex=0; it.hasNext(); columnIndex++) {
	MetaProperty p = (MetaProperty) it.next();
	String label = p.getQualifiedLabel(request);
	int columnWidth = subview.getCollectionColumnWidth(columnIndex);
	String width = columnWidth<0 || !resizeColumns?"":"data-width=" + columnWidth;
%>
	<th class="ox-list-header ox-padding-right-0">
		<div id="<xava:id name='<%=idCollection%>'/>_col<%=columnIndex%>" class="<%=((resizeColumns)?("xava_resizable"):(""))%>" <%=width%>>
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
	/* tmr
	String actionOnClick = Actions.getActionOnClick(
		request.getParameter("application"), request.getParameter("module"), 
		onSelectCollectionElementAction, f, viewName, idRow,
		"", "", 
		onSelectCollectionElementMetaAction, tabObject);
	*/	
%>
<td class="<%=cssCellClass%>" width="5">
<%-- tmr
<input type="checkbox" name="<xava:id name='xava_selected'/>" value="<%=propertyPrefix%>__SELECTED__:<%=f%>" <%=actionOnClick%>/>
--%>
<%-- tmr ini --%>
<input class="xava_selected" type="checkbox" name="<xava:id name='xava_selected'/>" value="<%=propertyPrefix%>__SELECTED__:<%=f%>" 
	data-on-select-collection-element-action="<%=onSelectCollectionElementAction%>"
	data-row="<%=f%>"
	data-view-object="<%=viewName%>"
	data-tab-object="<%=tabObject%>"
	data-confirm-message="<%=Is.empty(onSelectCollectionElementMetaAction)?"":onSelectCollectionElementMetaAction.getConfirmMessage()%>"
	data-takes-long="<%=Is.empty(onSelectCollectionElementMetaAction)?false:onSelectCollectionElementMetaAction.isTakesLong()%>"
/>
<%-- tmr fin --%>
</td>
<%
	it = subview.getMetaPropertiesList().iterator();	
	for (int columnIndex = 0; it.hasNext(); columnIndex++) { 
		MetaProperty p = (MetaProperty) it.next();
		String align =p.isNumber() && !p.hasValidValues()?"ox-text-align-right":"";
		int columnWidth = subview.getCollectionColumnWidth(columnIndex);
		String width = columnWidth<0 || !resizeColumns?"":"data-width=" + columnWidth; 
		String fvalue = null;
		Object value = null;
		String propertyName = p.getName();
		value = Maps.getValueFromQualifiedName(row, propertyName);
		fvalue = WebEditors.format(request, p, value, errors, view.getViewName(), true);	
		Object title = WebEditors.formatTitle(request, p, value, errors, view.getViewName(), true); 
%>
	<td class="<%=cssCellClass%> <%=align%> ox-list-data-cell">
	<xava:link action="<%=lineAction%>" argv='<%="row="+f + ",viewObject="+viewName%>'>
	<div title="<%=title%>" class="<xava:id name='tipable'/> <xava:id name='<%=idCollection%>'/>_col<%=columnIndex%>" <%=width%>>
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
 
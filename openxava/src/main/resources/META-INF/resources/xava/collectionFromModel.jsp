<%@ include file="imports.jsp"%>

<%@page import="org.openxava.controller.meta.MetaAction"%>
<%@page import="org.openxava.web.Ids"%>
<%@page import="org.openxava.controller.meta.MetaControllers"%>
<%@page import="org.openxava.util.Is"%>
<%@page import="org.openxava.util.XavaPreferences"%>

<%
org.openxava.util.Messages messages = (org.openxava.util.Messages) request.getAttribute("messages");
if (messages == null) {
    messages = new org.openxava.util.Messages();
    request.setAttribute("messages", messages);
}
org.openxava.controller.ModuleManager manager = (org.openxava.controller.ModuleManager) context.get(request, "manager", "org.openxava.controller.ModuleManager");
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
	%>
	<input type="checkbox" name="<xava:id name='xava_selected_all'/>" value="<%=propertyPrefix%>selected_all" 
		data-on-select-collection-element-action="<%=onSelectCollectionElementAction%>"
		data-view-object="<%=idCollection%>"
		data-prefix="<%=propertyPrefix%>"
		data-tab-object="<%=tabObject%>"/>
	</th>
<%
	// Heading
boolean singleLineHeader = false; // By now a fixed value 	
Iterator it = subview.getMetaPropertiesList().iterator();
for (int columnIndex=0; it.hasNext(); columnIndex++) {
	MetaProperty p = (MetaProperty) it.next();
	String label = p.getQualifiedLabel(request);
	int columnWidth = subview.getCollectionColumnWidth(columnIndex);
	String width = columnWidth<0 || !resizeColumns?"":"data-width=" + columnWidth;
%>
	<th class="ox-list-header ox-padding-right-0">
		<div id="<xava:id name='<%=idCollection%>'/>_col<%=columnIndex%>" class="<%=((resizeColumns)?("xava_resizable"):(""))%>" <%=width%>>
		<%if (singleLineHeader && resizeColumns) {%><nobr><%}%>
		<%=label%>&nbsp;
		<%if (singleLineHeader && resizeColumns) {%></nobr><%}%>
		</div>
	</th>
<%
	}
%>
</tr>

<%
	// Values
Collection aggregates = subview.getCollectionValues();
View parent = view.getParent();
boolean parentHasSections = parent != null && parent.hasSections();
boolean condition = (view.isKeyEditable() && parentHasSections && !view.isRepresentsEntityReference()); 
if (aggregates == null || condition) aggregates = java.util.Collections.EMPTY_LIST;
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
	<%	if (sortable) { %>
	<i class="xava_handle mdi mdi-swap-vertical"></i>	
	<%  } %>	
<xava:action action="<%=lineAction%>" argv='<%="row="+f + ",viewObject="+viewName%>'/>
<% 		
		if (style.isSeveralActionsPerRow()) {
			Collection rowActionNames;
			rowActionNames = view.removeUnavailableActionFromRow(subview.getRowActionsNames(), (",viewObject="+viewName));
			boolean hasIconOrImage = view.isRowActionHaveIcon(rowActionNames);
			// tmr if (rowActionNames.size() < 2) {
			if (rowActionNames.size() < XavaPreferences.getInstance().getRowActionsPopupThreshold() - 1) { // tmr
				for (java.util.Iterator itRowActions = rowActionNames.iterator(); itRowActions.hasNext(); ) { 	
					String rowAction = (String) itRowActions.next();		
%>
<xava:action action='<%=rowAction%>' argv='<%="row=" + f + ",viewObject="+viewName%>'/>
<%
				}
			} else {
%>
<a class="ox-image-link xava_popup_menu_icon">
	<i class="mdi mdi-dots-vertical"></i>
</a>

<ul class="ox-popup-menu ox-image-link ox-display-none">
<%		
				for (java.util.Iterator itRowActions = rowActionNames.iterator(); itRowActions.hasNext(); ) { 	
					String rowActionString = (String) itRowActions.next();			
%>
	<li>
		<jsp:include page="../barButton.jsp">
			<jsp:param name="action" value="<%=rowActionString%>"/>
			<jsp:param name="addSpaceWithoutImage" value="<%=hasIconOrImage%>"/>
			<jsp:param name="argv" value='<%="row=" + f + ",viewObject="+viewName%>'/>
		</jsp:include>
	</li>
<%
				}
%>
</ul>
<%
			}
		} 
%>
</nobr>
</td>
<%
	}
%>
<td class="<%=cssCellClass%>" width="5">
<input class="xava_selected" type="checkbox" name="<xava:id name='xava_selected'/>" value="<%=propertyPrefix%>__SELECTED__:<%=f%>" 
	data-on-select-collection-element-action="<%=onSelectCollectionElementAction%>"
	data-row-id="<%=idRow%>"
	data-row="<%=f%>"
	data-view-object="<%=viewName%>"
	data-tab-object="<%=tabObject%>"
	data-confirm-message="<%=Is.empty(onSelectCollectionElementMetaAction)?"":onSelectCollectionElementMetaAction.getConfirmMessage()%>"
	data-takes-long="<%=Is.empty(onSelectCollectionElementMetaAction)?false:onSelectCollectionElementMetaAction.isTakesLong()%>"
/>
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
 
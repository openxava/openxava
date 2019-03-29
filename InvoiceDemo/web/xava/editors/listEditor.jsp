<%@ include file="../imports.jsp"%>

<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Map"%>
<%@ page import="org.openxava.util.Labels"%>
<%@ page import="org.openxava.tab.impl.IXTableModel" %>
<%@ page import="org.openxava.tab.Tab"%>
<%@ page import="org.openxava.util.Strings" %>
<%@ page import="org.openxava.util.XavaPreferences" %>
<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.web.WebEditors" %>
<%@ page import="org.openxava.util.Is" %>
<%@ page import="org.openxava.web.Ids" %>
<%@ page import="org.openxava.controller.meta.MetaAction" %>
<%@ page import="org.openxava.controller.meta.MetaControllers" %>
<%@ page import="org.openxava.web.Actions" %>
<%@ page import="org.openxava.util.Users" %>
<%@ page import="java.util.prefs.Preferences" %>
<%@ page import="org.openxava.util.XavaResources" %> 

<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
org.openxava.controller.ModuleManager manager = (org.openxava.controller.ModuleManager) context.get(request, "manager", "org.openxava.controller.ModuleManager");
String collection = request.getParameter("collection"); 
String id = "list";
String collectionArgv = "";
String prefix = "";
String tabObject = request.getParameter("tabObject");
String scrollId = "list_scroll"; 
tabObject = (tabObject == null || tabObject.equals(""))?"xava_tab":tabObject;
if (collection != null && !collection.equals("")) {
	id = collection;
	collectionArgv=",collection="+collection;
	prefix = tabObject + "_";
	scrollId = "collection_scroll"; 
}
org.openxava.tab.Tab tab = (org.openxava.tab.Tab) context.get(request, tabObject);
tab.setIgnorePageRowCount(!style.isChangingPageRowCountAllowed());
String action=request.getParameter("rowAction");
action=action==null?manager.getEnvironment().getValue("XAVA_LIST_ACTION"):action;
String viewObject = request.getParameter("viewObject");
String actionArgv = viewObject != null && !viewObject.equals("")?",viewObject=" + viewObject:"";
viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject; 
org.openxava.view.View view = (org.openxava.view.View) context.get(request, viewObject);
String sonlyOneActionPerRow = request.getParameter("onlyOneActionPerRow");
java.util.Collection rowActions = null;
if (sonlyOneActionPerRow == null || !Boolean.parseBoolean(sonlyOneActionPerRow)) {
	rowActions = view.isRepresentsCollection()?view.getRowActionsNames():manager.getRowActionsNames();
}
else {
	rowActions = java.util.Collections.EMPTY_SET;
}
String sfilter = request.getParameter("filter");
boolean filter = !"false".equals(sfilter);
String displayFilter = tab.isFilterVisible()?"":"none";
String lastRow = request.getParameter("lastRow");
boolean singleSelection="true".equalsIgnoreCase(request.getParameter("singleSelection"));
String onSelectCollectionElementAction = view.getOnSelectCollectionElementAction();
MetaAction onSelectCollectionElementMetaAction = Is.empty(onSelectCollectionElementAction) ? null : MetaControllers.getMetaAction(onSelectCollectionElementAction);
String selectedRowStyle = style.getSelectedRowStyle();
String rowStyle = "border-bottom: 1px solid;";
int currentRow = ((Number) context.get(request, "xava_row")).intValue(); 
String cssCurrentRow = style.getCurrentRow();
int totalSize = -1; 
if (request.getAttribute(org.openxava.tab.Tab.TAB_RESETED_PREFIX + tab) == null) {
	tab.setRequest(request);
	if (!Is.emptyString(collection)) {
		tab.setTabObject(tabObject);
		tab.setConditionParameters();
	}
	tab.reset();
	request.setAttribute(org.openxava.tab.Tab.TAB_RESETED_PREFIX + tab, Boolean.TRUE); 
}
boolean resizeColumns = style.allowsResizeColumns() && tab.isResizeColumns();
String browser = request.getHeader("user-agent");
boolean scrollSupported = !(browser != null && (browser.indexOf("MSIE 6") >= 0 || browser.indexOf("MSIE 7") >= 0));
String styleOverflow = org.openxava.web.Lists.getOverflow(browser, tab.getMetaProperties());
boolean sortable = !Is.emptyString(collection) && view.isRepresentsSortableCollection();  
boolean simple = sortable;
if (simple) filter = false;
String groupBy = tab.getGroupBy();
boolean grouping = !Is.emptyString(groupBy);
if (grouping) action = null;
%>

<input type="hidden" name="xava_list<%=tab.getTabName()%>_filter_visible"/>

<% if (scrollSupported) { %>
<%String resizeColumnClass = resizeColumns?style.getResizeColumns():""; %>
<div class="<xava:id name='<%=scrollId%>'/> <%=resizeColumnClass%>" style="<%=styleOverflow%>">
<% } %> 
<table id="<xava:id name='<%=id%>'/>" class="xava_sortable_column <%=style.getList()%>" <%=style.getListCellSpacing()%> style="<%=style.getListStyle()%>">
<% if (sortable) { %><tbody class="xava_sortable_row"><% } %> 
<tr class="<%=style.getListHeader()%>">
<th class="<%=style.getListHeaderCell()%>" style="text-align: center">
<nobr>
	<% if (tab.isCustomizeAllowed()) { %>
	<a  id="<xava:id name='<%="customize_" + id%>'/>" href="javascript:openxava.customizeList('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>', '<%=id%>')" title="<xava:message key='customize_list'/>" class="<%=style.getActionImage()%>">
		<i class="mdi mdi-settings"></i>
	</a>
	<% if (filter) { %> 
	<a id="<xava:id name='<%="show_filter_" + id%>'/>" style='display: <%=tab.isFilterVisible()?"none":""%>' href="javascript:openxava.setFilterVisible('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>', '<%=id%>', '<%=tabObject%>', true)" title="<xava:message key='show_filters'/>">
		<i id="<xava:id name='<%="filter_image_" + id%>'/>" class="mdi mdi-filter"></i>
	</a>
	<a id="<xava:id name='<%="hide_filter_" + id%>'/>" style='display: <%=tab.isFilterVisible()?"":"none"%>' href="javascript:openxava.setFilterVisible('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>', '<%=id%>', '<%=tabObject%>', false)" title="<xava:message key='hide_filters'/>">
		<i id="<xava:id name='<%="filter_image_" + id%>'/>" class="mdi mdi-filter-remove"></i>  
	</a>	
	<% } // if (filter) %>	
	<%
		if (tab.isCustomizeAllowed()) { 
	%>
	<span class="<xava:id name='<%="customize_" + id%>'/>" style="display: none;">
	<xava:image action="List.addColumns" argv="<%=collectionArgv%>"/>
	</span>	
	<%
		}
	} 
	%>
</nobr> 
</th>
<th class="<%=style.getListHeaderCell()%>" width="5">
	<%
		if (!singleSelection){
			String actionOnClickAll = Actions.getActionOnClickAll(
			request.getParameter("application"), request.getParameter("module"), 
			onSelectCollectionElementAction, viewObject, prefix,
			selectedRowStyle, rowStyle, tabObject);
	%>
	<input type="checkbox" name="<xava:id name='xava_selected_all'/>" value="<%=prefix%>selected_all" <%=actionOnClickAll%> />
	<%
		}
	%>
</th>
<%
java.util.Collection properties = tab.getMetaProperties();
java.util.Iterator it = properties.iterator();
int columnIndex = 0;
Preferences preferences = Users.getCurrentPreferences();
while (it.hasNext()) {
	MetaProperty property = (MetaProperty) it.next();
	String align = "";
	if (style.isAlignHeaderAsData()) {
		align =property.isNumber() && !property.hasValidValues()?"vertical-align: middle;text-align: right":"vertical-align: middle";
	}
	int columnWidth = tab.getColumnWidth(columnIndex);
	String width = columnWidth<0 || !resizeColumns?"":"width: " + columnWidth + "px";
%>
<th class="<%=style.getListHeaderCell()%>" style="<%=align%>; padding-right: 0px" data-property="<%=property.getQualifiedName()%>">
<% if (resizeColumns) { %> <nobr> <% } %> 
<div id="<xava:id name='<%=id%>'/>_col<%=columnIndex%>" class="<%=((resizeColumns)?("xava_resizable"):("")) %>" style="overflow: hidden; <%=width%>" >
<%
	if (tab.isCustomizeAllowed()) {
%>
<span class="<xava:id name='<%="customize_" + id%>'/>" style="display: none;">
<i class="xava_handle mdi mdi-cursor-move"></i>
</span>
<%
	}
%>
<%
	String label = property.getQualifiedLabel(request);
	if (resizeColumns) label = label.replaceAll(" ", "&nbsp;");
	if (property.isCalculated() || sortable) { 
%>
<%=label%>&nbsp;
<%
	} else {
%>
<span class="<%=style.getListOrderBy()%>">
<%
String icon = property.isNumber()?"mdi mdi-sort-numeric":"mdi mdi-sort-alphabetical";
if (tab.isOrderAscending(property.getQualifiedName())) icon = style.getSortIndicator() + " mdi mdi-arrow-up-bold";
else if (tab.isOrderDescending(property.getQualifiedName())) icon = style.getSortIndicator() + " mdi mdi-arrow-down-bold";
else if (tab.isOrderAscending2(property.getQualifiedName())) icon = style.getSortIndicator2() + " mdi mdi-arrow-up-bold";
else if (tab.isOrderDescending2(property.getQualifiedName())) icon = style.getSortIndicator2() + " mdi mdi-arrow-down-bold";
String headerLabel=Strings.noLastToken(label) + " <nobr>" + Strings.lastToken(label) + "<i class='" + icon + "'></i></nobr>"; 
%>
<xava:link action='List.orderBy' argv='<%="property="+property.getQualifiedName() + collectionArgv%>'><%=headerLabel%></xava:link>&nbsp;
</span>
<%
		}
		   
		   if (tab.isCustomizeAllowed()) {
	%>
	<span class="<xava:id name='<%="customize_" + id%>'/>" style="display: none;">
	<xava:action action="List.changeColumnName" argv='<%="property="+property.getQualifiedName() + collectionArgv%>'/>
	<a href="javascript:openxava.removeColumn('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>', '<xava:id name='<%=id%>'/>_col<%=columnIndex%>', '<%=tabObject%>')" title="<xava:message key='remove_column'/>">
		<i class="mdi mdi-close-circle"></i>
	</a>
	</span>
<%
	}
%>
</div> 
<% if (resizeColumns) { %> </nobr> <% } %>
</th>
<%
	columnIndex++;
}
%>
</tr>
<%
	if (filter) {
%>
<tr id="<xava:id name='<%="list_filter_" + id%>'/>" class="xava_filter <%=style.getListSubheader()%>" style="display: <%=displayFilter%>">
<td class="<%=style.getFilterCell()%> <%=style.getListSubheaderCell()%>"> 
<xava:action action="List.filter" argv="<%=collectionArgv%>"/>
</td> 
<td class=<%=style.getListSubheaderCell()%> width="5"> 
	<a title='<xava:message key="clear_condition_values"/>' href="javascript:void(0)">
		<i class="mdi mdi-eraser" 
			id="<xava:id name='<%=prefix + "xava_clear_condition"%>' />" 
			onclick="openxava.clearCondition('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>', '<%=prefix%>')"></i>		
	</a>
</td> 
<%
it = properties.iterator();
String [] conditionValues = tab.getConditionValues();
String [] conditionValuesTo = tab.getConditionValuesTo(); 
String [] conditionComparators = tab.getConditionComparators();
int iConditionValues = -1;
columnIndex = 0; 
while (it.hasNext()) {
	MetaProperty property = (MetaProperty) it.next();
	if (!property.isCalculated()) {
		iConditionValues++; 
		boolean isValidValues = property.hasValidValues();
		boolean isString = "java.lang.String".equals(property.getType().getName());
		boolean isBoolean = "boolean".equals(property.getType().getName()) || "java.lang.Boolean".equals(property.getType().getName());
		boolean isDate = java.util.Date.class.isAssignableFrom(property.getType()) && !property.getType().equals(java.sql.Time.class);
		boolean isTimestamp = property.isTypeOrStereotypeCompatibleWith(java.sql.Timestamp.class);  
		String editorURLDescriptionsList = WebEditors.getEditorURLDescriptionsList(tab.getTabName(), tab.getModelName(), Ids.decorate(request, property.getQualifiedName()), iConditionValues, prefix, property.getQualifiedName(), property.getName());
		int maxLength = 100; 		
		int length = Math.min(isString?property.getSize()*4/5:property.getSize(), 20);
		String value= conditionValues==null?"":conditionValues[iConditionValues];
		String valueTo= conditionValuesTo==null?"":conditionValuesTo[iConditionValues];
		String comparator = conditionComparators==null?"":Strings.change(conditionComparators[iConditionValues], "=", Tab.EQ_COMPARATOR);
		int columnWidth = tab.getColumnWidth(columnIndex);
		String width = columnWidth<0 || !resizeColumns?"":"width: " + columnWidth + "px;";
		String paddingRight = resizeColumns?"padding-right: 12px;":"padding-right: 2px;"; 
%>
<td class="<%=style.getListSubheaderCell()%>" align="left">
<div class="<xava:id name='<%=id%>'/>_col<%=columnIndex%>" style="overflow: hidden; <%=width%> <%=paddingRight%>">
<% 		
		if (isValidValues) {
%>
<%-- Boolean.toString( ) for base0 is needed in order to work in WebSphere 6 --%>
<jsp:include page="comparatorsValidValuesCombo.jsp">
	<jsp:param name="validValues" value="<%=property.getValidValuesLabels(request)%>" />
	<jsp:param name="value" value="<%=value%>" />
	<jsp:param name="base0" value="<%=Boolean.toString(!property.isNumber())%>" />
	<jsp:param name="prefix" value="<%=prefix%>"/>
	<jsp:param name="index" value="<%=iConditionValues%>"/>
</jsp:include>		
<%
		}
		else if (!Is.empty(editorURLDescriptionsList)) {
%>
<jsp:include page="<%=editorURLDescriptionsList%>" >
	<jsp:param name="value" value="<%=value%>" />
</jsp:include>
<%
		}
		else if (isBoolean) {
%>
<jsp:include page="comparatorsBooleanCombo.jsp">
	<jsp:param name="comparator" value="<%=comparator%>" />
	<jsp:param name="prefix" value="<%=prefix%>"/>
	<jsp:param name="index" value="<%=iConditionValues%>"/> 
</jsp:include>
<%
		} else { // Not boolean
	String idConditionValue = Ids.decorate(request, prefix + "conditionValue." + iConditionValues);	
	String idConditionValueTo = Ids.decorate(request, prefix + "conditionValueTo." + iConditionValues);
	boolean isEmptyComparator = "empty_comparator".equals(comparator) || "not_empty_comparator".equals(comparator);
	String styleConditionValue =  isEmptyComparator ? "display: none;" : "display: inline;";	
	String styleConditionValueTo = "range_comparator".equals(comparator) ? "display: inline; " : "display: none;";
	String labelFrom = "range_comparator".equals(comparator) ? Labels.get("from") : "";
	String labelTo = Labels.get("to");
	String urlComparatorsCombo = "comparatorsCombo.jsp" // in this way because websphere 6 has problems with jsp:param
		+ "?comparator=" + comparator
		+ "&isString=" + isString
		+ "&isDate=" + isDate
		+ "&prefix=" + prefix  
		+ "&index=" + iConditionValues
		+ "&idConditionValue=" + idConditionValue
		+ "&idConditionValueTo=" + idConditionValueTo;
	String classConditionValue = isDate?"class='" + style.getDateCalendar() + "'":""; 
	if (isEmptyComparator) {
%>
<br/>
<%  } %>
<% String styleXavaComparator = Is.emptyString(value) && !isEmptyComparator?"style='display: none'":""; %>
<span class="xava_comparator" <%=styleXavaComparator%>> 
<jsp:include page="<%=urlComparatorsCombo%>" />
<br/> 
</span> 
<nobr <%=classConditionValue%>>
<input id="<%=idConditionValue%>" name="<%=idConditionValue%>" class=<%=style.getEditor()%> type="text" maxlength="<%=maxLength%>" size="<%=length%>" value="<%=value%>" placeholder="<%=labelFrom%>" style="<%=styleConditionValue%>; width: 100%; box-sizing: border-box; -moz-box-sizing: border-box; -webkit-box-sizing: border-box;"/><% if (isDate) { %><a href="javascript:showCalendar('<%=idConditionValue%>', '<%=org.openxava.util.Dates.dateFormatForJSCalendar(org.openxava.util.Locales.getCurrent(), isTimestamp)%>'<%=isTimestamp?", '12'":""%>)" style="position: relative; right: 25px; <%=styleConditionValue%>" tabindex="999"><i class="mdi mdi-<%=isTimestamp?"calendar-clock":"calendar"%>"></i></a>
<% } %>
</nobr>
<br/> 
<nobr <%=classConditionValue%>>
<input id="<%=idConditionValueTo%>" name="<%=idConditionValueTo%>" class=<%=style.getEditor()%> type="text" maxlength="<%=maxLength%>" size="<%=length%>" value="<%=valueTo%>" placeholder="<%=labelTo%>" style="<%=styleConditionValueTo%>; width: 100%; box-sizing: border-box; -moz-box-sizing: border-box; -webkit-box-sizing: border-box;"/><% if (isDate) { %><a style="position: relative; right: 25px; <%=styleConditionValueTo%>" href="javascript:showCalendar('<%=idConditionValueTo%>', '<%=org.openxava.util.Dates.dateFormatForJSCalendar(org.openxava.util.Locales.getCurrent(), isTimestamp)%>'<%=isTimestamp?", '12'":""%>)" tabindex="999"><i class="mdi mdi-<%=isTimestamp?"calendar-clock":"calendar"%>"></i></a>
<% } %>
</nobr>
	<%			
		}
	%>
</div>
</td>
<%
	}
	else {
%>
<th class=<%=style.getListSubheaderCell()%>>
	<div class="<xava:id name='<%=id%>'/>_col<%=columnIndex%>"/>
</th>
<%
	}
	columnIndex++; 
} // while
%>
</tr>
<%
	} /* if (filter) */
%>
<%
if (tab.isRowsHidden()) {
%>
	<tr id="nodata"><td align="center">
	<xava:link action="List.showRows" argv="<%=collectionArgv%>"/>
	</td></tr>
<%
	}
else {
IXTableModel model = tab.getTableModel(); 
totalSize = totalSize < 0?tab.getTotalSize():totalSize; 
if (totalSize > 0 || !Is.emptyString(collection)) { 
int finalIndex = simple?Integer.MAX_VALUE:tab.getFinalIndex();
for (int f=tab.getInitialIndex(); f<model.getRowCount() && f < finalIndex; f++) {
	String checked=tab.isSelected(f)?"checked='true'":"";	
	String cssClass=f%2==0?style.getListPair():style.getListOdd();	
	String cssCellClass=f%2==0?style.getListPairCell():style.getListOddCell(); 
	String cssStyle = tab.getStyle(f);
	if (cssStyle != null) {
		cssClass = cssClass + " " + cssStyle; 
		if (style.isApplySelectedStyleToCellInList()) cssCellClass = cssCellClass + " " + cssStyle; 
	}
	String events=f%2==0?style.getListPairEvents():style.getListOddEvents(); 
	String cssClassToActionOnClick = cssClass;
	if (tab.isSelected(f)){
		cssClass = "_XAVA_SELECTED_ROW_ " + cssClass; 
		rowStyle = rowStyle + " " + selectedRowStyle;
	}
	String prefixIdRow = Ids.decorate(request, prefix);	
%>
<tr id="<%=prefixIdRow%><%=f%>" class="<%=cssClass%>" <%=events%> style="<%=rowStyle%>">
	<td class="<%=cssCellClass%>" style="vertical-align: middle;text-align: center; <%=style.getListCellStyle()%>">
	<nobr> 
	<%if (sortable) { %>
	<i class="xava_handle mdi mdi-swap-vertical"></i>	
	<%}%>	
<%
	if (!org.openxava.util.Is.emptyString(action)) {  
%>
<xava:action action='<%=action%>' argv='<%="row=" + f + actionArgv%>'/>
<%
	}
	if (style.isSeveralActionsPerRow() && !grouping) {
		for (java.util.Iterator itRowActions = rowActions.iterator(); itRowActions.hasNext(); ) { 	
			String rowAction = (String) itRowActions.next();		
%>
			<xava:action action='<%=rowAction%>' argv='<%="row=" + f + actionArgv%>'/>
<%
		}
	}
	String actionOnClick = Actions.getActionOnClick(
		request.getParameter("application"), request.getParameter("module"), 
		onSelectCollectionElementAction, f, viewObject, prefixIdRow + f,
		selectedRowStyle, rowStyle, 
		onSelectCollectionElementMetaAction, tabObject);
%>
	</nobr> 
	</td>
	<td class="<%=cssCellClass%>" style="<%=style.getListCellStyle()%>">
	<input type="<%=singleSelection?"radio":"checkbox"%>" name="<xava:id name='xava_selected'/>" value="<%=prefix + "selected"%>:<%=f%>" <%=checked%> <%=actionOnClick%>/>
	</td>	
<%
	for (int c=0; c<model.getColumnCount(); c++) {
		MetaProperty p = tab.getMetaProperty(c);
		String align =p.isNumber() && !p.hasValidValues()?"vertical-align: middle;text-align: right; ":"vertical-align: middle; ";
		String cellStyle = align + style.getListCellStyle();
		int columnWidth = tab.getColumnWidth(c);		 		
		String width = columnWidth<0 || !resizeColumns?"":"width: " + columnWidth + "px"; 
		String fvalue = null;
		fvalue = WebEditors.format(request, p, model.getValueAt(f, c), errors, view.getViewName(), true);
		Object title = WebEditors.formatTitle(request, p, model.getValueAt(f, c), errors, view.getViewName(), true); 
%>
	<td class="<%=cssCellClass%>" style="<%=cellStyle%>; padding-right: 0px">
		<% if (style.isRowLinkable()) { %> 	
		<xava:link action='<%=action%>' argv='<%="row=" + f + actionArgv%>' cssClass='<%=cssStyle%>' cssStyle="text-decoration: none; outline: none">
			<div title="<%=title%>" class="<xava:id name='tipable'/> <xava:id name='<%=id%>'/>_col<%=c%>" style="overflow: hidden; <%=width%>">
				<%if (resizeColumns) {%><nobr><%}%>
				<%=fvalue%>&nbsp;
				<%if (resizeColumns) {%></nobr><%}%>
			</div>
		</xava:link>
		<% } else { %>		
		<div title="<%=title%>" class="<xava:id name='tipable'/> <xava:id name='<%=id%>'/>_col<%=c%>" style="overflow: hidden; <%=width%>">
			<%if (resizeColumns) {%><nobr><%}%>
			<%=fvalue%>&nbsp;
			<%if (resizeColumns) {%></nobr><%}%>
		</div>
		<% } %>
	</td>
<%
	}
%>
</tr>
<%
} 
%>
<tr class="<%=style.getTotalRow()%>">
<td style="<%=style.getTotalEmptyCellStyle()%>"/>
<td style="<%=style.getTotalEmptyCellStyle()%>"/>
<%
for (int c=0; c<model.getColumnCount(); c++) {
	MetaProperty p = tab.getMetaProperty(c);
	String align =p.isNumber() && !p.hasValidValues()?"text-align: right; ":"";	
	String cellStyle = align + style.getTotalCellStyle();
	int columnWidth = tab.getColumnWidth(c);		 		
	String width = columnWidth<0 || !resizeColumns?"":"width: " + columnWidth + "px";
	
	if (tab.hasTotal(c)) {
		Object total = tab.getTotal(c); 
		String ftotal = WebEditors.format(request, p, total, errors, view.getViewName(), true);
	%>
	<td class="<%=style.getTotalCell()%>" style="<%=cellStyle%>; padding-right: 0px">
		<div class="<xava:id name='<%=id%>'/>_col<%=c%>" style="overflow: hidden; <%=width%>">
			<nobr>
			<% if (!tab.isFixedTotal(c) && XavaPreferences.getInstance().isSummationInList()) { %>
				<xava:action action='List.removeColumnSum' argv='<%="property="+p.getQualifiedName() + collectionArgv%>'/>
			<% } %>
			<%
			if (view.isRepresentsCollection()) {
				org.openxava.view.View rootView = view.getParent().getCollectionRootOrRoot();
				String sumProperty =  collection + "." + p.getName() + "_SUM_";
				if (rootView.isPropertyUsedInCalculation(sumProperty)) {
					String script = org.openxava.web.Collections.sumPropertyScript(request, rootView, sumProperty);
			%>
					<input id="<xava:id name='<%=sumProperty%>'/>" type="hidden" value="<%=total%>" <%=script%>/>
			<%
				}
			}
			%>
			<%=ftotal%>&nbsp;
			</nobr>
		</div>
	</td>	
	<%	
	}
	else if (XavaPreferences.getInstance().isSummationInList() && tab.isTotalCapable(c)) { 
	%>
	<td class="<%=style.getTotalCapableCell()%>" style="<%=style.getTotalCapableCellStyle() %>">
		<div class="<xava:id name='<%=id%>'/>_col<%=c%>" style="overflow: hidden; <%=width%>">
			<xava:action action='List.sumColumn' argv='<%="property="+p.getQualifiedName() + collectionArgv%>'/>&nbsp;
		</div>	
	</td>
	<%
	}
	else if (tab.hasTotal(c + 1)) { 
	%>
	<td class="<%=style.getTotalLabelCell()%>" style="<%=style.getTotalLabelCellStyle()%>">
		<div class="<xava:id name='<%=id%>'/>_col<%=c%>" style="overflow: hidden; <%=width%>">
		<%=tab.getTotalLabel(0, c + 1)%>&nbsp;
		</div>	
	</td>
	<%
	}	
	else {
	%>	 
	<td style="<%=style.getTotalEmptyCellStyle()%>"/>
	<%		
	}	
}
%>
</tr>
<%
int additionalTotalsCount = tab.getAdditionalTotalsCount() + 1;
for (int i=1; i<additionalTotalsCount; i++) {
%>
<tr class="<%=style.getTotalRow()%>">
<td style="<%=style.getTotalEmptyCellStyle()%>"/>
<td style="<%=style.getTotalEmptyCellStyle()%>"/>
<%
for (int c=0; c<model.getColumnCount(); c++) {
	MetaProperty p = tab.getMetaProperty(c);
	String align =p.isNumber() && !p.hasValidValues()?"text-align: right; ":"";
	String cellStyle = align + style.getTotalCellStyle(); 
	int columnWidth = tab.getColumnWidth(c);		 		
	String width = columnWidth<0 || !resizeColumns?"":"width: " + columnWidth + "px";
	if (tab.hasTotal(i, c)) {
	%> 	
		<td class="<%=style.getTotalCell()%>" style="<%=cellStyle%>">
			<div class="<xava:id name='<%=id%>'/>_col<%=c%>" style="overflow: hidden; <%=width%>">
				<jsp:include page="collectionTotal.jsp">
					<jsp:param name="row" value="<%=i%>"/>
					<jsp:param name="column" value="<%=c%>"/>
					<jsp:param name="viewObject" value="xava_view"/>
				</jsp:include>
			</div>	
		</td>	
	<%	
	}
	else if (tab.hasTotal(i, c + 1)) { 
	%>
	<td class="<%=style.getTotalLabelCell()%>" style="<%=style.getTotalLabelCellStyle()%>">
		<div class="<xava:id name='<%=id%>'/>_col<%=c%>" style="overflow: hidden; <%=width%>">		
		<%=tab.getTotalLabel(i, c + 1)%>&nbsp;
		</div>
		<%@ include file="listEditorTotalActionsExt.jsp"%>
	</td>
	<%	
	}
	else {
	%>	 
	<td style="<%=style.getTotalEmptyCellStyle()%>">
		<div class="<xava:id name='<%=id%>'/>_col<%=c%>"/>
	</td>
	<%		
	}	
}
%>
</tr>
<%
} // for additionalTotalsCount 
%>
<%@ include file="listEditorLastRowExt.jsp"%>
<%
}
else {
%>
<tr id="nodata"><td class="<%=totalSize==0?style.getMessages():style.getErrors()%>">
<% if (totalSize == 0) { %>
<b><xava:message key="no_objects"/></b>
<% } else { %>
<b><xava:message key="list_error"/></b>
<% } %>
</td></tr>
<%
}
}

if (lastRow != null) {
%>
<tr>
	<jsp:include page="<%=lastRow%>"/>
</tr>
<%
}
%>
<% if (sortable) { %></tbody><% } %> 
</table>
<% if (scrollSupported) { %>
</div> 
<% } %>

<% if (!tab.isRowsHidden() && !simple) { %>
<table width="100%" class="<%=style.getListInfo()%>" cellspacing=0 cellpadding=0>
<tr class='<%=style.getListInfoDetail()%>'>
<td class='<%=style.getListInfoDetail()%>'>
<%
int last=tab.getLastPage();
int current=tab.getPage();
if (current > 1) {
%>
<span class='<%=style.getFirst()%>'><span class='<%=style.getPageNavigationArrow()%>' <%=style.getPreviousPageNavigationEvents(Ids.decorate(request, id))%>><xava:image action='List.goPreviousPage' argv='<%=collectionArgv%>'/></span></span>
<%
}
else {
%>
<span class='<%=style.getFirst()%>'><span class='<%=style.getPageNavigationArrowDisable()%>'>
<i class="mdi mdi-menu-left"></i>
</span></span>
<%	
} 
%>
<span class="<%=style.getPageNavigationPages()%>">
<%
for (int i=1; i<=last; i++) {
if (i == current) {
	if (style.isShowPageNumber()) {  
%>
<span class="<%=style.getPageNavigationSelected()%>"><%=i%></span>
	<% } else {%>
<span class="<%=style.getPageNavigationSelected()%>">
	<img 
		src='<%=request.getContextPath()%>/<%=style.getImagesFolder()%>/<%=style.getPageNavigationSelectedImage()%>' 
		border=0 align="absmiddle"/>
</span>	
	<% } %>
<% } else { 
		if (style.isShowPageNumber()) { 
%>
<xava:link action='List.goPage' argv='<%="page=" + i + collectionArgv%>' cssClass="<%=style.getPageNavigation()%>"><%=i%></xava:link>
<% 
		} else {
%>
<span class="<%=style.getPageNavigation()%>">
	<img 
		src='<%=request.getContextPath()%>/<%=style.getImagesFolder()%>/<%=style.getPageNavigationImage()%>' 
		border=0 align="absmiddle"/>
</span>
<%				
		}
	}
} 
%>
</span>
<%
if (!tab.isLastPage()) {
%>
<span class='<%=style.getLast()%>'>
<span class='<%=style.getPageNavigationArrow()%>' <%=style.getNextPageNavigationEvents(Ids.decorate(request, id)) %>>
<xava:image action='List.goNextPage' argv='<%=collectionArgv%>'/>
</span>
</span>
<% 
} 
else {
%>
<span class='<%=style.getLast()%>'>
<span class='<%=style.getPageNavigationArrowDisable()%>'>
<i class="mdi mdi-menu-right"></i>
</span>
</span>
<%	
} 
%>
<% if (style.isChangingPageRowCountAllowed()) { %>
&nbsp;
<select id="<xava:id name='<%=id + "_rowCount"%>'/>" class=<%=style.getEditor()%>
	onchange="openxava.setPageRowCount('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>', '<%=collection==null?"":collection%>', this)">
	<% 
	int [] rowCounts = { 5, 10, 12, 15, 20 }; // The peformance with more than 20 rows is poor for page reloading
	for (int i=0; i<rowCounts.length; i++) {
		String selected = rowCounts[i] == tab.getPageRowCount()?"selected='selected'":""; 	
	%>	
	<option value="<%=rowCounts[i]%>" <%=selected %>><%=rowCounts[i]%></option>
	<%
	}
	%>
</select>
<span class="<%=style.getRowsPerPage()%>">	 
<xava:message key="rows_per_page"/>
</span>
<% } // of if (style.isChangingPageRowCountAllowed()) %>
</td>
<td style='text-align: right; vertical-align: middle' class='<%=style.getListInfoDetail()%>'>
<% if (XavaPreferences.getInstance().isShowCountInList() && !style.isShowRowCountOnTop() && !grouping) { %> 
<xava:message key="list_count" intParam="<%=totalSize%>"/>
<% } %>
<% if (collection == null && style.isHideRowsAllowed() && !grouping) { %> 
(<xava:link action="List.hideRows" argv="<%=collectionArgv%>"/>)
<% } %>
</td>
</tr>
</table>
<% } %>

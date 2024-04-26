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
<%@ page import="org.openxava.view.View" %>
<%@ page import="org.openxava.web.Ids" %>
<%@ page import="org.openxava.controller.meta.MetaAction" %>
<%@ page import="org.openxava.controller.meta.MetaControllers" %>
<%@ page import="org.openxava.util.Users" %>
<%@ page import="java.util.prefs.Preferences" %>
<%@ page import="org.openxava.util.XavaResources" %>
<%@ page import="org.openxava.web.EditorsEvents"%>

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
String lastRow = request.getParameter("lastRow");
boolean singleSelection="true".equalsIgnoreCase(request.getParameter("singleSelection"));
String onSelectCollectionElementAction = view.getOnSelectCollectionElementAction();
MetaAction onSelectCollectionElementMetaAction = Is.empty(onSelectCollectionElementAction) ? null : MetaControllers.getMetaAction(onSelectCollectionElementAction);
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
boolean sortable = !Is.emptyString(collection) && view.isRepresentsSortableCollection();  
boolean simple = sortable;
if (simple) filter = false;
String groupBy = tab.getGroupBy();
boolean grouping = !Is.emptyString(groupBy);
if (grouping) action = null;
boolean editable = ("true").equals(request.getParameter("viewKeyEditable"));
%>

<input type="hidden" name="xava_list<%=tab.getTabName()%>_filter_visible"/>

<%String resizeColumnClass = resizeColumns?style.getResizeColumns():""; %>
<div class="<xava:id name='<%=scrollId%>'/> <%=resizeColumnClass%> ox-overflow-auto">
<table id="<xava:id name='<%=id%>'/>" class="xava_sortable_column ox-list" <%=style.getListCellSpacing()%>>
<% if (sortable) { %><tbody class="xava_sortable_row"><% } %> 
<tr class="ox-list-header">
<th class="ox-list-header ox-text-align-center">
<nobr>
	<% if (tab.isCustomizeAllowed()) { %>
	<a  id="<xava:id name='<%="customize_" + id%>'/>" class="xava_customize_list <%=style.getActionImage()%>" 
		title="<xava:message key='customize_list'/>" data-id="<%=id%>">
		<i class="mdi mdi-settings"></i>
	</a>
	<% } %>
	<% if (filter) { %> 
	<a id="<xava:id name='<%="show_filter_" + id%>'/>" class='<%=tab.isFilterVisible()?"xava_show_hide_filter ox-display-none":"xava_show_hide_filter"%>'  
		title="<xava:message key='show_filters'/>"
		data-id="<%=id%>" data-tab-object="<%=tabObject%>" data-visible="true">
		<i id="<xava:id name='<%="filter_image_" + id%>'/>" class="mdi mdi-filter"></i>
	</a>
	<a id="<xava:id name='<%="hide_filter_" + id%>'/>" class='<%=tab.isFilterVisible()?"xava_show_hide_filter":"xava_show_hide_filter ox-display-none"%>' 
		title="<xava:message key='hide_filters'/>"
		data-id="<%=id%>" data-tab-object="<%=tabObject%>" data-visible="false">
		<i id="<xava:id name='<%="filter_image_" + id%>'/>" class="mdi mdi-filter-remove"></i>  
	</a>	
	<% } // if (filter) %>	
	<%
	if (tab.isCustomizeAllowed()) { 
	%>
	<span class="<xava:id name='<%="customize_" + id%>'/> ox-display-none">
	<xava:image action="List.addColumns" argv="<%=collectionArgv%>"/>
	</span>	
	<%
	}
	%>
</nobr> 
</th>
<th class="ox-list-header" width="5">
	<%
		if (!singleSelection){
	%>
	<input type="checkbox" name="<xava:id name='xava_selected_all'/>" value="<%=prefix%>selected_all" 
		data-on-select-collection-element-action="<%=onSelectCollectionElementAction%>"
		data-view-object="<%=viewObject%>"
		data-prefix="<%=prefix%>"
		data-tab-object="<%=tabObject%>"/>
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
		align =property.isNumber() && !property.hasValidValues()?"ox-text-align-right":"";
	}
	int columnWidth = tab.getColumnWidth(columnIndex);
	String width = columnWidth<0 || !resizeColumns?"":"data-width=" + columnWidth;
%>
<th class="ox-list-header ox-padding-right-0 ox-vertical-align-middle <%=align%>" data-property="<%=property.getQualifiedName()%>">
<% if (resizeColumns) { %> <nobr> <% } %> 
<div id="<xava:id name='<%=id%>'/>_col<%=columnIndex%>" class="<%=((resizeColumns)?("xava_resizable"):("")) %>" <%=width%>>
<%
	if (tab.isCustomizeAllowed()) {
%>
<span class="<xava:id name='<%="customize_" + id%>'/> ox-display-none">
<i class="xava_handle mdi mdi-cursor-move"></i>
</span>
<%
	}
%>
<%
	String label = property.getQualifiedLabel(request);
	if (resizeColumns) label = label.replaceAll(" ", "&nbsp;");
	if (!tab.isOrderCapable(property) || sortable) { 
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
	<span class="<xava:id name='<%="customize_" + id%>'/> ox-display-none ox-column-customize-controls-on-right">
	<xava:action action="List.changeColumnName" argv='<%="property="+property.getQualifiedName() + collectionArgv%>'/>
	<a class="xava_remove_column" title="<xava:message key='remove_column'/>"
		data-column="<xava:id name='<%=id%>'/>_col<%=columnIndex%>" data-tab-object="<%=tabObject%>">	
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
<tr id="<xava:id name='<%="list_filter_" + id%>'/>" class="xava_filter <%=style.getListSubheader()%> <%=tab.isFilterVisible()?"":"ox-display-none"%>">
<td class="<%=style.getFilterCell()%> ox-list-subheader"> 
<xava:action action="List.filter" argv="<%=collectionArgv%>"/>
</td> 
<td class="ox-list-subheader" width="5"> 
	<a title='<xava:message key="clear_condition_values"/>' href="javascript:void(0)">
		<i class="xava_clear_condition mdi mdi-eraser" 
			id="<xava:id name='<%=prefix + "xava_clear_condition"%>' />" 
			data-prefix="<%=prefix%>"></i>		
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
	if (property.isFilterCapable()) { 
		iConditionValues++; 
		boolean isValidValues = property.hasValidValues();
		boolean isString = "java.lang.String".equals(property.getType().getName());
		boolean isBoolean = "boolean".equals(property.getType().getName()) || "java.lang.Boolean".equals(property.getType().getName());
		boolean isDate = java.util.Date.class.isAssignableFrom(property.getType()) && !property.getType().equals(java.sql.Time.class) || java.time.LocalDate.class.isAssignableFrom(property.getType());   
		boolean isTimestamp = property.isCompatibleWith(java.sql.Timestamp.class); 
		String editorURLDescriptionsList = WebEditors.getEditorURLDescriptionsList(tab.getTabName(), tab.getModelName(), Ids.decorate(request, property.getQualifiedName()), iConditionValues, prefix, property.getQualifiedName(), property.getName());
		int maxLength = 100; 		
		int length = Math.min(isString?property.getSize()*4/5:property.getSize(), 20);
		String value= conditionValues==null?"":conditionValues[iConditionValues];
		String valueTo= conditionValuesTo==null?"":conditionValuesTo[iConditionValues];
		String comparator = conditionComparators==null?"":Strings.change(conditionComparators[iConditionValues], "=", Tab.EQ_COMPARATOR);
		int columnWidth = tab.getColumnWidth(columnIndex);
		String width = columnWidth<0 || !resizeColumns?"":"data-width=" + columnWidth;
		String paddingRight = resizeColumns?"ox-list-subheader-resize":"ox-list-subheader-no-resize"; 
%>
<td class="ox-list-subheader" align="left">
<div class="<xava:id name='<%=id%>'/>_col<%=columnIndex%> <%=paddingRight%>" <%=width%>>
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
	String styleConditionValue =  isEmptyComparator ? "ox-display-none" : "ox-display-inline";	
	String styleConditionValueTo = "range_comparator".equals(comparator) ? "ox-display-inline" : "ox-display-none";
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
	String classConditionValue = "";
	String dateDisabled = ""; 
	String styleCalendar = "";
	if (isDate) { 
		if (Is.anyEqual(comparator, "year_comparator", "year_month_comparator", "month_comparator")) {
			classConditionValue="class='ox-date-calendar'";
			dateDisabled = "xava_date_disabled";
			styleCalendar = "ox-display-none"; 
		} else {
			classConditionValue="class='xava_date ox-date-calendar'";  
		}
	}
	String attrConditionValue = isDate?"data-date-format='" + org.openxava.util.Dates.dateFormatForJSCalendar(isTimestamp) + "'":"";
	if (isTimestamp) attrConditionValue += " data-enable-time='true'"; 
	if (isEmptyComparator) {
%>
<br/>
<%  } %>
<% String styleXavaComparator = Is.emptyString(value) && !isEmptyComparator?"ox-display-none":""; %>
<span class="xava_comparator <%=styleXavaComparator%>"> 
<jsp:include page="<%=urlComparatorsCombo%>" />
<br/> 
</span>
<%-- WARNING: IF YOU CHANGE THE NEXT CODE PASS THE MANUAL TEST ON DateCalendarTest.txt --%> 
<nobr <%=classConditionValue%> <%=attrConditionValue%>>
<input id="<%=idConditionValue%>" name="<%=idConditionValue%>" class="<%=style.getEditor()%> <%=dateDisabled%> <%=styleConditionValue%>" type="text"
	maxlength="<%=maxLength%>" size="<%=length%>" value="<%=value%>" placeholder="<%=labelFrom%>"
	<%=isDate?"data-input":""%>/>
	<% if (isDate) { 
	String styleValue = styleCalendar != null ? styleCalendar : styleConditionValue;
	%>
		<a href="javascript:void(0)" data-toggle class="<%=styleValue%>" tabindex="999"><i class="mdi mdi-<%=isTimestamp?"calendar-clock":"calendar"%>"></i></a>
	<% } %>
</nobr>
<br/>
<%-- WARNING: IF YOU CHANGE THE NEXT CODE PASS THE MANUAL TEST ON DateCalendarTest.txt --%> 
<nobr <%=classConditionValue%> <%=attrConditionValue%>>
<input id="<%=idConditionValueTo%>" name="<%=idConditionValueTo%>" class="<%=style.getEditor()%> <%=styleConditionValueTo%>" type="text" 
	maxlength="<%=maxLength%>" size="<%=length%>" value="<%=valueTo%>" placeholder="<%=labelTo%>"
	<%=isDate?"data-input":""%>/>
	<% if (isDate) { %>
		<a href="javascript:void(0)" data-toggle class="<%=styleConditionValueTo%>" tabindex="999"><i class="mdi mdi-<%=isTimestamp?"calendar-clock":"calendar"%>"></i></a>
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
<td class="ox-list-subheader">
	<div class="<xava:id name='<%=id%>'/>_col<%=columnIndex%>"/>
</td>
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

System.out.println("listEditor");
System.out.println(editable); // false -- true
System.out.println(view.getModelName()); // EntityA - EntityA
System.out.println(view.hasKeyProperties());
System.out.println(view.getRoot().hasKeyProperties());
System.out.println("-----");
System.out.println(view.isRepresentsEntityReference());
if (view.getParent() != null) System.out.println(view.getParent().isRepresentsEntityReference()); // 
System.out.println("***");
System.out.println(view.getRoot().hasSections()); // true -- 
System.out.println("###");
if (view.getParent() != null) System.out.println(view.getParent().hasSections()); // true -- 

boolean parentHasSections = false;
boolean parentIsEntityReference = true;
View parent = view.getParent();
if (parent != null) {
	parentHasSections = parent.hasSections();
	parentIsEntityReference = view.getParent().isRepresentsEntityReference();
}
editable = editable && view.getRoot().hasKeyProperties();
if (totalSize > 0 || !Is.emptyString(collection)) { 
int finalIndex = simple?Integer.MAX_VALUE:tab.getFinalIndex();
for (int f=tab.getInitialIndex(); f< (editable && parentHasSections && !parentIsEntityReference ? 0 : model.getRowCount()) && f < finalIndex; f++) {
	String checked=tab.isSelected(f)?"checked='true'":"";	
	String cssClass=f%2==0?"ox-list-pair":"ox-list-odd";	
	String cssCellClass=f%2==0?"ox-list-pair":"ox-list-odd"; 
	String cssStyle = tab.getStyle(f);
	if (cssStyle != null) {
		cssClass = cssClass + " " + cssStyle; 
		if (style.isApplySelectedStyleToCellInList()) cssCellClass = cssCellClass + " " + cssStyle; 
	}
	String events=f%2==0?style.getListPairEvents():style.getListOddEvents(); 
	String cssClassToActionOnClick = cssClass;
	if (tab.isSelected(f)){
		cssClass = "_XAVA_SELECTED_ROW_ " + cssClass; 
	}
	String prefixIdRow = Ids.decorate(request, prefix);	
%>
<tr id="<%=prefixIdRow%><%=f%>" class="<%=cssClass%>" <%=events%>>
	<td class="<%=cssCellClass%> ox-list-action-cell">
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
%>
	</nobr> 
	</td>
	<td class="<%=cssCellClass%>">
	<input class="xava_selected" type="<%=singleSelection?"radio":"checkbox"%>" name="<xava:id name='xava_selected'/>" 
		value="<%=prefix + "selected"%>:<%=f%>" <%=checked%>
		data-on-select-collection-element-action="<%=onSelectCollectionElementAction%>"
		data-row-id="<%=prefixIdRow%><%=f%>"
		data-row="<%=f%>"
		data-view-object="<%=viewObject%>"
		data-tab-object="<%=tabObject%>"
		data-confirm-message="<%=Is.empty(onSelectCollectionElementMetaAction)?"":onSelectCollectionElementMetaAction.getConfirmMessage()%>"
		data-takes-long="<%=Is.empty(onSelectCollectionElementMetaAction)?false:onSelectCollectionElementMetaAction.isTakesLong()%>"/>
	</td>	
<%
	for (int c=0; c<model.getColumnCount(); c++) {
		MetaProperty p = tab.getMetaProperty(c);
		String align =p.isNumber() && !p.hasValidValues() && !tab.isFromCollection(p)?"ox-text-align-right":""; 
		int columnWidth = tab.getColumnWidth(c);		 		
		String width = columnWidth<0 || !resizeColumns?"":"data-width=" + columnWidth;
		String widthClass = width.equals("")?"ox-width-100":"";  
		String fvalue = null;
		Object title = null;
		if (tab.isFromCollection(p)) {
			title = fvalue = Strings.toString(model.getValueAt(f, c));
		}
		else {
			fvalue = WebEditors.format(request, p, model.getValueAt(f, c), errors, view.getViewName(), true);
			title = WebEditors.formatTitle(request, p, model.getValueAt(f, c), errors, view.getViewName(), true);
		}
%>
	<td class="<%=cssCellClass%> <%=align%> ox-list-data-cell">
		<xava:link action='<%=action%>' argv='<%="row=" + f + actionArgv%>' cssClass='<%=cssStyle%>'>
			<div title="<%=title%>" class="<xava:id name='tipable'/> <xava:id name='<%=id%>'/>_col<%=c%> <%=widthClass%>" <%=width%>>
				<%if (resizeColumns) {%><nobr><%}%>
				<%=fvalue%><%if (resizeColumns) {%>&nbsp;<%}%>
				<%if (resizeColumns) {%></nobr><%}%>
			</div>
		</xava:link>
	</td>
<%
	}
%>
</tr>
<%
} 
%>
<tr class="<%=style.getTotalRow()%>">
<td/>
<td/>
<%
for (int c=0; c<model.getColumnCount(); c++) {
	MetaProperty p = tab.getMetaProperty(c);
	String align =p.isNumber() && !p.hasValidValues()?"ox-text-align-right":"";	
	int columnWidth = tab.getColumnWidth(c);		 		
	String width = columnWidth<0 || !resizeColumns?"":"data-width=" + columnWidth;
	
	if (tab.hasTotal(c)) {
		Object total = tab.getTotal(c); 
		String ftotal = WebEditors.format(request, p, total, errors, view.getViewName(), true);
	%>
	<td class="ox-total-cell ox-padding-right-0 <%=align%>">
		<div class="<xava:id name='<%=id%>'/>_col<%=c%>" <%=width%>>
			<nobr>
			<% if (!tab.isFixedTotal(c) && XavaPreferences.getInstance().isSummationInList()) { %>
				<xava:action action='List.removeColumnSum' argv='<%="property="+p.getQualifiedName() + collectionArgv%>'/>
			<% } %>
			<%
			if (view.isRepresentsCollection()) {
				org.openxava.view.View rootView = view.getParent().getCollectionRootOrRoot();
				String sumProperty =  collection + "." + p.getName() + "_SUM_";
				if (rootView.isPropertyUsedInCalculation(sumProperty)) {
			%>
					<input class="xava_onchange_calculate" id="<xava:id name='<%=sumProperty%>'/>" type="hidden" value="<%=ftotal%>"
						<%=EditorsEvents.onChangeCalculateDataAttributes(request.getParameter("application"), request.getParameter("module"), rootView, sumProperty)%>
					/>
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
	<td class="ox-total-capable-cell">
		<div class="<xava:id name='<%=id%>'/>_col<%=c%>" <%=width%>>
			<xava:action action='List.sumColumn' argv='<%="property="+p.getQualifiedName() + collectionArgv%>'/>&nbsp;
		</div>	
	</td>
	<%
	}
	else if (tab.hasTotal(c + 1)) { 
	%>
	<td class="ox-total-label-cell">
		<div class="<xava:id name='<%=id%>'/>_col<%=c%>" <%=width%>>
		<%=tab.getTotalLabel(0, c + 1)%>&nbsp;
		</div>	
	</td>
	<%
	}	
	else {
	%>	 
	<td/>
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
<td/>
<td/>
<%
for (int c=0; c<model.getColumnCount(); c++) {
	MetaProperty p = tab.getMetaProperty(c);
	String align =p.isNumber() && !p.hasValidValues()?"ox-text-align-right":"";
	int columnWidth = tab.getColumnWidth(c);		 		
	String width = columnWidth<0 || !resizeColumns?"":"data-width=" + columnWidth;
	if (tab.hasTotal(i, c)) {
	%> 	
		<td class="ox-total-cell <%=align%>">
			<div class="<xava:id name='<%=id%>'/>_col<%=c%>" <%=width%>>
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
	<td class="ox-total-label-cell">
		<div class="<xava:id name='<%=id%>'/>_col<%=c%>" <%=width%>>		
		<%=tab.getTotalLabel(i, c + 1)%>&nbsp;
		</div>
		<%@ include file="listEditorTotalActionsExt.jsp"%>
	</td>
	<%	
	}
	else {
	%>	 
	<td>
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
<tr id="nodata"><td class='<%=totalSize==0?style.getMessages():"ox-errors"%>'>
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
</div> 

<% if (!tab.isRowsHidden() && !simple) { %>
<table width="100%" class="<%=style.getListInfo()%>" cellspacing=0 cellpadding=0>
<tr class='ox-list-info-detail'>
<td class='ox-list-info-detail'>
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
<select id="<xava:id name='<%=id + "_rowCount"%>'/>" class="<%=style.getEditor()%> xava_set_page_row_count"
	data-collection='<%=collection==null?"":collection%>'>
	<% 
	int [] rowCounts = { 5, 10, 12, 15, 20, 50 }; // The peformance with more than 50 rows is poor for page reloading
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
<td class='ox-list-info-detail'>
<% if (XavaPreferences.getInstance().isShowCountInList() && !style.isShowRowCountOnTop() && !grouping && totalSize < Integer.MAX_VALUE) { %> 
<xava:message key="list_count" intParam="<%=totalSize%>"/>
<% } %>
<% if (collection == null && style.isHideRowsAllowed() && !grouping && totalSize < Integer.MAX_VALUE) { %> 
(<xava:link action="List.hideRows" argv="<%=collectionArgv%>"/>)
<% } %>
</td>
</tr>
</table>
<% } %>

<%@ include file="../imports.jsp"%>

<%@page import="java.util.Iterator"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashSet"%>
<%@page import="org.openxava.controller.meta.MetaAction"%>
<%@page import="org.openxava.web.Ids"%>
<%@page import="org.openxava.controller.meta.MetaControllers"%>
<%@page import="org.openxava.util.Is"%>
<%@page import="org.openxava.util.Maps"%>
<%@page import="org.openxava.util.XavaPreferences"%>
<%@page import="org.openxava.view.View"%>
<%@page import="org.openxava.model.meta.MetaProperty"%>
<%@page import="org.openxava.model.meta.MetaReference"%> 
<%@page import="org.openxava.web.DescriptionsLists"%> 
<%@page import="org.openxava.web.WebEditors"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
String collectionName = request.getParameter("collectionName");
String viewObject = request.getParameter("viewObject");
String viewName = viewObject + "_" + collectionName;
View view = (View) context.get(request, viewObject);
View subview = view.getSubview(collectionName);
String idCollection = org.openxava.web.Collections.id(request, collectionName);
String propertyPrefixAccumulated = request.getParameter("propertyPrefix");
String propertyPrefix = propertyPrefixAccumulated == null?collectionName + ".":propertyPrefixAccumulated + collectionName + ".";

boolean resizeColumns = style.allowsResizeColumns() && XavaPreferences.getInstance().isResizeColumns();
String collectionClass = subview.isEditable()?"ox-element-collection":"";
boolean sortable = subview.isCollectionSortable();
String removeSelectedAction = subview.getRemoveSelectedCollectionElementsAction();
boolean suppressRemoveAction = removeSelectedAction != null && "".equals(removeSelectedAction);
boolean labelOnEachCell = "true".equals(request.getParameter("labelOnEachCell"));
boolean hideTotals = "true".equals(request.getParameter("hideTotals"));
List<MetaProperty> metaPropertiesList = subview.getMetaPropertiesList();
List<MetaProperty> keyPropertiesList = subview.addKeyPropertiesOfReferencesEntity();
Set<MetaProperty> metaPropertiesSet = new HashSet<>(metaPropertiesList);
for (MetaProperty metaProperty : keyPropertiesList) {
	if (metaPropertiesSet.add(metaProperty)) {
		metaPropertiesList.add(metaProperty);
	}
}
%>
<div class="<%=collectionClass%> ox-overflow-auto">
<% if (resizeColumns) { %> 
<div class="<xava:id name='collection_scroll'/>">
<% } %>
<table id="<xava:id name='<%=idCollection%>'/>" class="ox-list" <%=style.getListCellSpacing()%>>
<% if (sortable) { %><tbody class="xava_sortable_elements"><% } %> 
<tr class="ox-list-header">
	<% if (subview.isCollectionEditable()) { %>
	<th class="ox-list-header" width="5"/>
	<% } %>
<%
	// Heading
Iterator it = metaPropertiesList.iterator();
for (int columnIndex=0; it.hasNext(); columnIndex++) {
	MetaProperty p = (MetaProperty) it.next();
	boolean isHiddenKey = keyPropertiesList.contains(p);
	String label = p.getQualifiedLabel(request);
	int columnWidth = subview.getCollectionColumnWidth(columnIndex);
	String width = columnWidth<0 || !resizeColumns?"":"data-width=" + columnWidth;
	MetaReference ref = null;	
	if (p.getName().contains(".")) {
		String refName = org.openxava.util.Strings.noLastTokenWithoutLastDelim(p.getName(), ".");
		ref = subview.getMetaReference(refName);
	}
	String headerId = "";
	String dataDefaultValue = "";	
	if (p.hasNotDependentDefaultValueCalculator() || ref != null && ref.hasNotDependentDefaultValueCalculator()) { 	
		Object defaultValue = null; 
		String propertyId = null;
		if (ref != null && subview.displayAsDescriptionsList(ref) && ref.getMetaModelReferenced().getAllKeyPropertiesNames().size() > 1) {
			java.util.Map refValues = subview.getSubview(ref.getName()).getValues();  
			defaultValue = p.getMetaModel().toString(refValues);
			propertyId = ref.getName() + DescriptionsLists.COMPOSITE_KEY_SUFFIX;
		}
		else {
			if (ref != null && subview.displayAsDescriptionsList(ref) && !p.isKey()) {
				MetaProperty key = (MetaProperty) p.getMetaModel().getMetaPropertiesKey().iterator().next();
				p = key.cloneMetaProperty();
				p.setName(ref.getName() + "." + key.getName());
			}		
			if (org.openxava.web.WebEditors.mustToFormat(p, subview.getViewName())) {				
				Object value = subview.getDefaultValueInElementCollection(p.getName());
				defaultValue = value==null?null:org.openxava.web.WebEditors.formatToStringOrArray(request, p, value, errors, subview.getViewName(), false); 
				propertyId = p.getName();
			}		
		}
		if (defaultValue instanceof String) { // We don't support arrays by now
			dataDefaultValue = "data-default-value='" + defaultValue + "'";
			String id = Ids.decorate(request, collectionName + ".H." + propertyId);			
			headerId = "id='" + id + "'";
		}
	}
%>
	<th <%=headerId%> <%=dataDefaultValue%> class="ox-list-header ox-padding-right-0" <%=isHiddenKey? "hidden" : ""%>>
		<div id="<xava:id name='<%=idCollection%>'/>_col<%=columnIndex%>" class="<%=((resizeColumns)?("xava_resizable"):(""))%>" <%=width%>>
		<%if (resizeColumns) {%><nobr><%}%>
		<%=label%>&nbsp;
		<%if (resizeColumns) {%></nobr><%}%>
		</div>
	</th>
	<% if (ref != null && subview.isSearchForReference(ref) && subview.isLastSearchKey(p.getName())) { %>
	<th></th>
	<% } %>
<%
	}
%>
</tr>

<%
	// Values
Collection values = subview.getCollectionValues();
if (values == null) values = java.util.Collections.EMPTY_LIST;
int rowCount = subview.isCollectionEditable()?values.size() + 2:values.size(); 
for (int f=0; f < rowCount; f++) {
	String cssClass=f%2==0?"ox-list-pair":"ox-list-odd";
	String cssCellClass=f%2==0?"ox-list-pair":"ox-list-odd";
	String idRow = Ids.decorate(request, propertyPrefix) + f;	
	String events=f%2==0?style.getListPairEvents():style.getListOddEvents();
	String newRowStyle = subview.isCollectionEditable() && f == rowCount - 1?"ox-display-none":"";
	String lastRowClass = subview.isCollectionEditable() && f >= rowCount - 2?"xava_onchange_last_row":"";  
	String actionsStyle = subview.isCollectionEditable() && f >= rowCount - 2?"class='ox-visibility-hidden'":"";
	String app = request.getParameter("application");
	String module = request.getParameter("module");
	boolean hasTotals = subview.hasCollectionTotals();
	String sortableClass = subview.isCollectionEditable() && f >= rowCount - 2?"":"xava_sortable_element_row";
%>
<tr id="<%=idRow%>" class="<%=cssClass%> <%=sortableClass%> <%=newRowStyle%>" <%=events%>>
<% if (subview.isCollectionEditable()) { %>
	<td class="<%=cssCellClass%> ox-list-action-cell">
	<nobr <%=actionsStyle%>>
	<%if (sortable) { %>
	<i class="xava_handle mdi mdi-swap-vertical"></i>
	<%}%>
	<%if (!Is.emptyString(removeSelectedAction)) {%>
	<xava:action action='<%=removeSelectedAction%>' argv='<%="row=" + f + ",viewObject=" + viewName%>'/>
	<%} else if (suppressRemoveAction){%>
	 <a title='<xava:message key="remove_row"/>' href="javascript:void(0)">
		<img 		 
			src='<%=request.getContextPath()%>/xava/images/spacer.gif'
			border='0' align='absmiddle'/>
	 </a>
	<%} else { %>
	 <a title='<xava:message key="remove_row"/>' href="javascript:void(0)" class='<%=style.getActionImage()%>'>
	 	<% if (style.isUseIconsInsteadOfImages()) { %>
		<i class="ox-element-collection-remove-action mdi mdi-delete" data-row="<%=f%>" data-has-totals="<%=hasTotals%>"></i>
		<% } else { %>
		<img class="ox-element-collection-remove-action" 		 
			src='<%=request.getContextPath()%>/xava/images/delete.gif'
			border='0' align='absmiddle'
			data-row="<%=f%>" data-has-totals="<%=hasTotals%>"/>
		<% } %>		
	 </a>
	<%} %>	
	</nobr>
	</td>
<% } %>
<%
	it = metaPropertiesList.iterator();	
	for (int columnIndex = 0; it.hasNext(); columnIndex++) { 
		MetaProperty p = (MetaProperty) it.next();
		boolean isHiddenKey = keyPropertiesList.contains(p);
		String align =p.isNumber() && !p.hasValidValues()?"ox-text-align-right":""; 
		int columnWidth = subview.getCollectionColumnWidth(columnIndex);
		String width = columnWidth<0 || !resizeColumns?"":"data-width=" + columnWidth;
		String referenceName = null;
		String searchAction = null;
		if (p.getName().contains(".")) {
			String refName = org.openxava.util.Strings.noLastTokenWithoutLastDelim(p.getName(), ".");
			if (subview.displayAsDescriptionsList(subview.getMetaReference(refName))) {
				referenceName = collectionName + "." + f + "." + refName;
			}
			else { 			
				View refView = subview.getSubview(refName);
				if (refView.isSearch()) searchAction = refView.getSearchAction();
			}
		}
		String propertyName = collectionName + "." + f + "." + p.getName();
		boolean throwPropertyChanged = subview.throwsPropertyChanged(p.getName());
		Object fvalue = null;
		if (!subview.isCollectionMembersEditables()) {
			Object value = view.getValue(propertyName); 
			fvalue = org.openxava.web.WebEditors.formatToStringOrArray(request, p, value, errors, view.getViewName(), false);
		}
%>
	<td class="<%=cssCellClass%> <%=align%> ox-list-data-cell" <%=isHiddenKey ? "hidden" : ""%>>
		<% if (labelOnEachCell) { %>
			<span class="<%=style.getLabel()%>"><%=p.getQualifiedLabel(request)%></span>
		<% } %>
		<div class="<xava:id name='<%=idCollection%>'/>_col<%=columnIndex%> <%=lastRowClass%>" <%=width%>
			data-row="<%=f%>">
		<nobr> 
		<% if (!subview.isCollectionMembersEditables()) {%>
			<% if (referenceName == null) { %>
				<%=fvalue%>&nbsp;
			<% } else { %>
				<xava:descriptionsList reference="<%=referenceName%>" readOnlyAsLabel="true"/>	
			<% } %>
		<% } else if (referenceName != null) { %>
		<xava:descriptionsList reference="<%=referenceName%>"/>
		<% } else { %>
		<span id="<xava:id name='<%="editor_" + view.getPropertyPrefix() + propertyName%>'/>" class="xava_editor">
		<xava:editor property="<%=propertyName%>" throwPropertyChanged="<%=throwPropertyChanged%>"/>
		</span>
		<% } %>	
	 	</nobr>  
		</div>
	</td>		
	<% if (searchAction != null && subview.isLastSearchKey(p.getName())) {	%>
	<td class="<%=cssCellClass%> <%=align%> ox-element-collection-search-action-cell">
		<xava:action action='<%=searchAction%>' argv='<%="keyProperty="+propertyName%>'/> 								
	</td>
	<% } %>
<%
	}
}
%>
</tr>
<% if (!hideTotals) { %> 
	<jsp:include page="collectionTotals.jsp" />
<% } %> 
<% if (sortable) { %></tbody><% } %>
</table>
<% if (resizeColumns) { %>
</div>
<% } %>
</div> 

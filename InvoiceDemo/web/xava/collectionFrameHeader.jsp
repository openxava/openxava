<%@ include file="imports.jsp"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%@ page import="org.openxava.web.WebEditors"%>
<%@ page import="org.openxava.view.View" %>
<%@ page import="org.openxava.tab.Tab" %>
<%@ page import="org.openxava.util.XavaPreferences" %>
<%@ page import="org.openxava.util.XavaResources" %>
<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.web.Ids" %>
<%@ page import="org.openxava.web.Collections" %>

<%
String collectionName = request.getParameter("collectionName");
String viewObject = request.getParameter("viewObject");
View view = (View) context.get(request, viewObject);
View collectionView = view.getSubview(collectionName);
String viewName = viewObject + "_" + collectionName;
String frameId = Ids.decorate(request, "frame_" + view.getPropertyPrefix() + collectionName);
String hiddenStyle = view.isFrameClosed(frameId)?"":"style='display: none'";
if (!collectionView.isCollectionFromModel()) {
	Tab tab = collectionView.getCollectionTab();
	if (request.getAttribute(Tab.TAB_RESETED_PREFIX + tab) == null) {
		String collectionId = Collections.id(request, collectionName);
		String tabObject = Collections.tabObject(collectionId);
		tab.setTabObject(tabObject);		
		tab.setRequest(request);  
		tab.setConditionParameters(); 
		tab.reset();
		request.setAttribute(Tab.TAB_RESETED_PREFIX + tab, Boolean.TRUE);
	}
}
%>
<span <%=hiddenStyle%>>
(<%=collectionView.getCollectionSize()%>)
<%
	if (collectionView.isCollectionEditable()) {
%>
<xava:image action='<%=collectionView.getNewCollectionElementAction()%>' argv='<%="viewObject="+viewName%>'/>
<%
	}
%>
</span>
<span class="<%=style.getFrameTotals()%>" <%=hiddenStyle%>>
<%
	int totalRows = collectionView.getCollectionTotalsCount();
int totalColumns = collectionView.getMetaPropertiesList().size();
for (int row=0; row<totalRows; row++) {	
	for (int column=0; column<totalColumns; column++) {
		if (collectionView.hasCollectionTotal(row, column)) {
	MetaProperty p = (MetaProperty) collectionView.getMetaPropertiesList().get(column);
	String ftotal = WebEditors.format(request, p, collectionView.getCollectionTotal(row, column), errors, view.getViewName(), true);
%>
	&nbsp;&nbsp;				
	<span class="<%=style.getFrameTotalsLabel()%>"><%=collectionView.getCollectionTotalLabel(row, column)%>:</span>
	<span class="<%=style.getFrameTotalsValue()%>"><%=ftotal%></span>
<%
	}
	}
}

if (!collectionView.isCollectionFromModel() && XavaPreferences.getInstance().isSummationInList()) {
	Tab tab = collectionView.getCollectionTab();
	int totalTabColumns = tab.getMetaProperties().size();
	for (int column=0; column<totalTabColumns; column++) {
		if (!tab.isFixedTotal(column) && tab.hasTotal(column)) {
	MetaProperty p = tab.getMetaProperty(column);
	String ftotal = WebEditors.format(request, p, tab.getTotal(column), errors, view.getViewName(), true);
	String label = XavaResources.getString(request, "sum_of", p.getLabel());
%>
	&nbsp;&nbsp;				
	<span class="<%=style.getFrameTotalsLabel()%>"><%=label%>:</span>
	<span class="<%=style.getFrameTotalsValue()%>"><%=ftotal%></span>
<%									
		}
	}
}
%>
</span>


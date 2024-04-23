<%
String tabObject = org.openxava.web.Collections.tabObject(idCollection); 
org.openxava.tab.Tab tab = subview.getCollectionTab();

String tabPrefix = tabObject + "_";
tab.clearStyle();
int selectedRow = subview.getCollectionEditingRow();
if (selectedRow >= 0) {
	String cssClass=selectedRow%2==0?style.getListPairSelected():style.getListOddSelected();
	tab.setStyle(selectedRow, cssClass);
}
context.put(request, tabObject, tab);

// The list
%>
<jsp:include page="../list.jsp">
	<jsp:param name="collection" value="<%=idCollection%>"/>
	<jsp:param name="rowAction" value="<%=lineAction%>"/>
	<jsp:param name="tabObject" value="<%=tabObject%>"/>
	<jsp:param name="viewObject" value="<%=viewName%>"/>
</jsp:include>

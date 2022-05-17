<%@ include file="../imports.jsp"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<%
String collection = request.getParameter("collection"); 
String id = "list";
String collectionArgv = "";
String prefix = "";
String tabObject = request.getParameter("tabObject"); 
tabObject = (tabObject == null || tabObject.equals(""))?"xava_tab":tabObject;
if (collection != null && !collection.equals("")) {
	id = collection;
	collectionArgv=",collection="+collection;
	prefix = tabObject + "_";	
}
org.openxava.tab.Tab tab = (org.openxava.tab.Tab) context.get(request, tabObject);
org.openxava.tab.impl.IXTableModel model = tab.getTableModel(); 
for (int r=tab.getInitialIndex(); r<model.getRowCount() && r < tab.getFinalIndex(); r++) {
%>
	<xava:link action="List.viewDetail"><div style="border: 2px solid rgb(130, 143, 149); display: inline-block; padding: 10px; margin-bottom: 10px;">
	<h4><%=model.getValueAt(r, 1)%>(<%=model.getValueAt(r, 0)%>)</h4>
	<%=model.getValueAt(r, 2)%><br/>
	<%=model.getValueAt(r, 3)%> (<%=model.getValueAt(r, 4)%>)	
	</div></xava:link>
<%
}
%>

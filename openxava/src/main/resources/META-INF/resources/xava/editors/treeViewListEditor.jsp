<%@ include file="../imports.jsp"%>
 
<%@page import="org.openxava.annotations.Tree"%>
<%@page import="org.openxava.view.View"%>
<%@page import="org.openxava.view.meta.MetaView"%>
<%@page import="org.openxava.view.meta.MetaCollectionView"%>
<%@page import="org.openxava.model.MapFacade"%>
<%@page import="org.openxava.web.Actions"%>
<%@page import="org.openxava.web.Ids" %>
<%@page import="org.openxava.controller.meta.MetaAction"%>
<%@page import="org.openxava.controller.meta.MetaControllers"%>
<%@page import="org.openxava.tab.impl.IXTableModel" %>
<%@page import="org.openxava.web.editors.TreeView" %>
<%@page import="org.openxava.web.editors.TreeViewActions" %>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Collections"%>
<%@page import="java.text.DateFormat"%>
<%@page import="org.openxava.util.Locales"%>
<%@page import="org.openxava.util.Is"%>
<%@page import="org.apache.commons.beanutils.PropertyUtils"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>
<jsp:useBean id="treeParser" class="org.openxava.web.editors.TreeViewParser" scope="session" />
<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>

<%
String viewObject = request.getParameter("viewObject"); // Id to access to the view object of the collection
View collectionView = (View) context.get(request, viewObject); // We get the collection view by means of context
View rootView = collectionView.getRoot(); // In this case we use the root view
String collectionName = request.getParameter("collectionName");
Map key = rootView.getKeyValues();
String action = request.getParameter("rowAction");
String actionArgv = ",viewObject=" + viewObject;
String actionArg = "viewObject=" + viewObject;
String actionWithArgs;
String tabObject = org.openxava.tab.Tab.COLLECTION_PREFIX + collectionName.replace('.', '_');
String prefix = tabObject + "_";
org.openxava.tab.Tab tab = collectionView.getCollectionTab();
tab.setRequest(request);
Map[] keyValues;
String prefixIdRow = Ids.decorate(request, prefix);
String xavaId = Ids.decorate(request, "xava_selected");
tab.reset();
context.put(request, tabObject, tab);
context.put(request, org.openxava.web.editors.TreeViewParser.XAVA_TREE_VIEW_PARSER, treeParser);
treeParser.createMetaTreeView(tab, viewObject, collectionName, style, errors);
String []parseData = treeParser.parse(tab.getModelName());
String javaScriptCode = parseData[0];
String indexList = parseData[1];
String module = request.getParameter("module");
String tableId = Ids.decorate(request.getParameter("application"), module, collectionName);
TreeViewActions metaTreeViewActions = new TreeViewActions(collectionView, treeParser.getMetaTreeView(tab.getModelName()));

String contextPath = (String) request.getAttribute("xava.contextPath");
if (contextPath == null) contextPath = request.getContextPath();
String version = org.openxava.controller.ModuleManager.getVersion();
MetaView metaView = rootView.getMetaModel().getMetaView(rootView.getViewName());
MetaCollectionView metaCollectionView = metaView.getMetaCollectionView(collectionName);
Tree tree = metaCollectionView.getPath();
String pathProperty = "path";
String idProperties = "";
boolean initialState = true;
if (tree != null) {
	pathProperty = tree.pathProperty() != null ? tree.pathProperty() : "path"; 
	idProperties = tree.idProperties() != null ? tree.idProperties() : "";
	initialState = tree.initialExpandedState();
}

if(!Is.empty(key)){
%>

	<div id = "openxavaInput_<%=collectionName%>" class="ox-tree-collection">
		<table id = "<%=tableId%>" name="treeTable_<%=collectionName%>">
			<tbody id = "<%=tableId%>_body" >
			<%
			int count = 0;
			String [] indexes = indexList.split(","); 
			for (int i=0; i<indexes.length; i++) { 
				String index = indexes[i]; 
				actionWithArgs = "row=" + index  + actionArgv;
				String indexId = prefixIdRow + index;
				String nodeId = xavaId + index;
				String nodeValue = prefix + "selected:" + index;
				String nodeRef = "openxava.executeAction('" +
					request.getParameter("application") + "', '" + request.getParameter("module") +"', '', false, '" + action + "', '" +
					actionWithArgs + "')";
				%>
				<tr id="<%=indexId%>">
				  <td>
				    <input type="checkbox" name="<%=xavaId%>" id="<%=nodeId%>"
				        value = "<%=nodeValue%>"/>
				    <a href = "<%=nodeRef%>">_</a>
				  </td>
				</tr>
				<%
				count++;
			}
				
			
			%>
		
			</tbody>
		</table>		
	</div>

	<div class="xava_tree" 
	data-collection-name="<%=collectionName%>" 
	data-application="<%=request.getParameter("application")%>" 
	data-module="<%=request.getParameter("module")%>" 
	data-action-argv="<%=actionArgv%>"
	data-action-arg="<%=actionArg%>"
	data-action="<%=action%>"
	data-xava-id="<%=xavaId%>"
	data-path-property="<%=pathProperty%>"
	data-table-id="<%=tableId%>"
	data-prefix="<%=prefix%>"
	data-id-properties="<%=idProperties%>"
	data-initial-state="<%=initialState%>"
	</div>

	<script type='text/javascript' <xava:nonce/> src='<%=contextPath%>/dwr/interface/Tree.js?ox=<%=version%>'>
	</script>
	<%
}
%>

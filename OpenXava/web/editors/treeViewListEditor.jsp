<%@ include file="../imports.jsp"%>
 
<%@page import="org.openxava.view.View"%>
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

if(!Is.empty(key)){
%>
	<xava:action action="<%=metaTreeViewActions.getUpAction()%>" argv="<%=actionArg%>" />
	<xava:action action="<%=metaTreeViewActions.getDownAction()%>" argv="<%=actionArg%>" />
	<xava:action action="<%=metaTreeViewActions.getLeftAction()%>" argv="<%=actionArg%>" />
	<xava:action action="<%=metaTreeViewActions.getRightAction()%>" argv="<%=actionArg%>" />
	<div id = "tree_<%=collectionName%>" class="ygtv-checkbox" >
	</div>

	<div id = "openxavaInput_<%=collectionName%>" style="visibility: hidden; height:0px">
		<table id = "<%=tableId%>" name="treeTable_<%=collectionName%>" style="height:0px">
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
				        value = "<%=nodeValue%>" style="height:0px" />
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

	<script type="text/javascript">
		$(document).ready(function(){
			var tree_<%=collectionName%> = {};
			tree_<%=collectionName%>.tree = <%=javaScriptCode%>
			tree_<%=collectionName%>.suppress = false; // this will prevent collapse/expand when clicking on label
			tree_<%=collectionName%>.loading = true; // this will prevent collapse/expand when loading
			tree_<%=collectionName%>.tree.render();
			tree_<%=collectionName%>.loading = false;

			tree_<%=collectionName%>.tree.subscribe("clickEvent", function(args) {
				tree_<%=collectionName%>.suppress=true;
				tree_<%=collectionName%>.tree.onEventToggleHighlight(args);
				node = args["node"];
				nodeIndex = node.data;
				var actionWithArgs = "row=" + nodeIndex  + "<%=actionArgv%>";
	
				// syncronize state with openxava hidden input item
				var htmlInput = document.getElementById("<%=xavaId%>" + node.data);
				if (htmlInput != null) {
					if (node.highlightState == 1){
						htmlInput.checked = true;
					} else {
						htmlInput.checked = false;
					}
				}
			});
			
			tree_<%=collectionName%>.tree.subscribe("dblClickEvent", function(args) {
				node = args["node"];
				tree_<%=collectionName%>.suppress=true; 
				var actionWithArgs = "row=" + (node.data)  + "<%=actionArgv%>";
				openxava.executeAction('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>', "", false, '<%=action%>', actionWithArgs);
			});
	
			tree_<%=collectionName%>.tree.subscribe("expand", function(node) {
				if (tree_<%=collectionName%>.suppress) {
					tree_<%=collectionName%>.suppress = false;
					return false;
				}
				if (!tree_<%=collectionName%>.loading) {
					var actionWithArgs = "row=" + (node.data)  + "<%=actionArgv%>";
					openxava.executeAction('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>', "", false, 'TreeView.expand', actionWithArgs);
				}
			});
	
			tree_<%=collectionName%>.tree.subscribe("collapse", function(node) {
				if (tree_<%=collectionName%>.suppress || tree_<%=collectionName%>.loading) {
					tree_<%=collectionName%>.suppress = false;
					return false;
				}
				if (!tree_<%=collectionName%>.loading) {
					var actionWithArgs = "row=" + (node.data)  + "<%=actionArgv%>";
					openxava.executeAction('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>', "", false, 'TreeView.collapse', actionWithArgs);
				}
			});
		})
	</script>
	
	<%
}
%>

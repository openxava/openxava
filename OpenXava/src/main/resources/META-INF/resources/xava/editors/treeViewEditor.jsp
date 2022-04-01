<%@ page import="org.openxava.view.View" %>
<%@ page import="org.openxava.view.meta.MetaCollectionView" %>
<%@ page import="org.openxava.view.meta.MetaView" %>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<%
String collectionName = request.getParameter("collectionName");
String viewObject = request.getParameter("viewObject");
View view = (View) context.get(request, viewObject);
View collectionView = view.getSubview(collectionName);
collectionView.addListAction("");
MetaView metaView = collectionView.getMetaModel().getMetaView(collectionView.getViewName());
MetaCollectionView metaCollectionView = metaView.getMetaCollectionView(collectionName);
if (metaCollectionView != null) {
	if (metaCollectionView.getNewActionName() == null && !collectionView.isRepresentsEntityReference()) {
		collectionView.setNewCollectionElementAction("TreeView.new");
	}
	if (metaCollectionView.getRemoveSelectedActionName() == null) {
		collectionView.setRemoveSelectedCollectionElementsAction("TreeView.removeSelected");
	}
	if (metaCollectionView.getRemoveActionName() == null) {
		collectionView.setRemoveCollectionElementAction("TreeView.remove");
	}
	if (metaCollectionView.getSaveActionName() == null) {
		collectionView.setSaveCollectionElementAction("TreeView.save");
	}
} else {
	if (collectionView.getNewCollectionElementAction().equals("Collection.new")) {
		collectionView.setNewCollectionElementAction("TreeView.new");
	}
	if (collectionView.getRemoveSelectedCollectionElementsAction().equals("Collection.removeSelected")) {
		collectionView.setRemoveSelectedCollectionElementsAction("TreeView.removeSelected");
	}
	if (collectionView.getRemoveCollectionElementAction().equals("Collection.remove")) {
		collectionView.setRemoveCollectionElementAction("TreeView.remove");
	}
	if (collectionView.getSaveCollectionElementAction().equals("Collection.save")) {
		collectionView.setSaveCollectionElementAction("TreeView.save");
	}
}

%>
<jsp:include page="collectionEditor.jsp">
	<jsp:param name="listEditor" value="treeViewListEditor.jsp"/>
</jsp:include>

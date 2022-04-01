<%@ page import="org.openxava.model.meta.MetaModel" %>
<%@ page import="org.openxava.model.meta.MetaMember" %>
<%@ page import="org.openxava.view.meta.MetaCollectionView"%>
<%@ page import="org.openxava.view.meta.MetaView"%>

<%@ page import="org.openxava.util.Is" %>
<%@ page import="org.openxava.util.Labels"%>
<%@ page import="org.openxava.util.Strings"%>

<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Collection"%>
<%@ page import="java.util.Iterator"%>

<%
String viewName = module.getViewName();
MetaModel model = MetaModel.get(module.getModelName());
java.util.Collection<String> collectionNames = model.getColectionsNames();
MetaView metaView = viewName == null ? model.getMetaViewByDefault() : model.getMetaView(viewName);

for (String collectionName : collectionNames) {
	MetaCollectionView mcv = metaView.getMetaCollectionView(collectionName);	
	if (mcv == null) continue;
	String prefix = collectionName + ":"; 
	String collectionLabel = Labels.get(collectionName) + ": ";
	Collection<String> actionNames = new ArrayList<String>();
	actionNames.add(prefix + (Is.emptyString(mcv.getNewActionName()) ? "Collection.new" : mcv.getNewActionName()));
	actionNames.add(prefix + (Is.emptyString(mcv.getAddActionName()) ? "Collection.add" : mcv.getAddActionName()));
	actionNames.add(prefix + (Is.emptyString(mcv.getHideActionName()) ? "Collection.hideDetail" : mcv.getHideActionName()));
	actionNames.add(prefix + (Is.emptyString(mcv.getSaveActionName()) ? "Collection.save" : mcv.getSaveActionName())); // saveAndStay?
	actionNames.add(prefix + (Is.emptyString(mcv.getRemoveActionName()) ? "Collection.remove" : mcv.getRemoveActionName()));
	actionNames.add(prefix + (Is.emptyString(mcv.getEditActionName()) ? "Collection.edit" : mcv.getEditActionName()));
	actionNames.add(prefix + (Is.emptyString(mcv.getRemoveSelectedActionName()) ? "Collection.removeSelected" : mcv.getRemoveSelectedActionName()));	
	if (!Is.emptyString(mcv.getOnSelectElementActionName())) actionNames.add(prefix + mcv.getOnSelectElementActionName());
		
	for (String listAction : (Collection<String>) mcv.getActionsListNames()) actionNames.add(prefix + listAction);
	MetaController controller = MetaControllers.getMetaController("DefaultListActionsForCollections");
	for (Iterator<MetaAction> it = controller.getAllMetaActions().iterator(); it.hasNext();) {
		MetaAction action = it.next();
		if (action.isHidden()) continue;
		actionNames.add(prefix + action.getQualifiedName());
	}
		
	for (String subController : (Collection<String>) mcv.getSubcontrollersListNames()) {
		controller = MetaControllers.getMetaController(subController);
		for (Iterator<MetaAction> it = controller.getAllMetaActions().iterator(); it.hasNext();) {
			MetaAction action = it.next();
			if (action.isHidden()) continue;
			actionNames.add(prefix + action.getQualifiedName());
		}
	}
	
	for (String rowAction : (Collection<String>) mcv.getActionsRowNames()) actionNames.add(prefix + rowAction);
	controller = MetaControllers.getMetaController("DefaultRowActionsForCollections");
	for (Iterator<MetaAction> it = controller.getAllMetaActions().iterator(); it.hasNext();) {
		MetaAction action = it.next();
		if (action.isHidden()) continue;
		actionNames.add(prefix + action.getQualifiedName());
	}
	
	for (String detailAction : (Collection<String>) mcv.getActionsDetailNames()) actionNames.add(prefix + detailAction);	
	
	for (String actionName : actionNames) {		
		String checked = actions.contains(actionName)?"checked='true'":"";
%>
	<td>
	<INPUT name="<%=propertyKey%>" type="checkbox" class="<%=style.getEditor()%>" 
		tabindex="1" 
		value="<%=actionName%>"	
		<%=checked%>	
		<%=disabled%>
		<%=script%>
	/>		
	<%=collectionLabel + Labels.get(Strings.change(actionName, prefix, ""))%> 
	<% if (++i % 4 == 0) { %></tr><tr><% } %>
	</td>		
<%	}
	
}
%>
<%@ include file="../imports.jsp"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.openxava.util.Maps" %>
<%@ page import="org.openxava.util.Is" %>
<%@ page import="org.openxava.util.XavaPreferences" %>
<%@ page import="org.openxava.view.View" %>
<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.model.meta.MetaReference" %>
<%@ page import="org.openxava.model.meta.MetaEntity" %>
<%@ page import="org.openxava.model.meta.MetaCollection" %>
<%@ page import="org.openxava.web.WebEditors" %>
<%@ page import="org.openxava.controller.meta.MetaController"%>

<%
String collectionName = request.getParameter("collectionName");
String viewObject = request.getParameter("viewObject");
String listEditor = request.getParameter("listEditor");  
View view = (View) context.get(request, viewObject);
View collectionView = view.getSubview(collectionName);
if (!Is.emptyString(listEditor)) {
	collectionView.setDefaultListActionsForCollectionsIncluded(false);
	collectionView.setDefaultRowActionsForCollectionsIncluded(false); 
}
View subview = view.getSubview(collectionName);
MetaReference ref = view.getMetaModel().getMetaCollection(collectionName).getMetaReference();
String viewName = viewObject + "_" + collectionName;
String propertyPrefixAccumulated = request.getParameter("propertyPrefix");
String idCollection = org.openxava.web.Collections.id(request, collectionName); 
boolean collectionEditable = subview.isCollectionEditable();
boolean collectionMembersEditables = subview.isCollectionMembersEditables();
boolean hasListActions = subview.hasListActions();
String lineAction = ""; 
if (collectionEditable || collectionMembersEditables) {
	lineAction = subview.getEditCollectionElementAction();
}
else {
	lineAction = subview.getViewCollectionElementAction();
}
String propertyPrefix = propertyPrefixAccumulated == null?collectionName + ".":propertyPrefixAccumulated + collectionName + "."; 
%>
<table width="100%" <%=style.getListCellSpacing()%>>
<%
	// New
if (view.displayDetailInCollection(collectionName)) {
	context.put(request, viewName, collectionView);
%>
<tr class=<%=style.getCollectionListActions()%>><td colspan="<%=subview.getMetaPropertiesList().size()+1%>" class=<%=style.getCollectionListActions()%>>
<%
	if (collectionEditable) {
%>
<% if (subview.isRepresentsEntityReference()) { %>
<%=org.openxava.web.render.ButtonRenderer.render(new org.openxava.web.render.ViewRenderContext(request, response, Map.of("action", subview.getAddCollectionElementAction(), "argv", "viewObject=" + viewName)))%>
<% } %>
<%=org.openxava.web.render.ButtonRenderer.render(new org.openxava.web.render.ViewRenderContext(request, response, Map.of("action", subview.getNewCollectionElementAction(), "argv", "viewObject=" + viewName)))%>
<% if (subview.isRepresentsEntityCollection()) { %>
<%=org.openxava.web.render.ButtonRenderer.render(new org.openxava.web.render.ViewRenderContext(request, response, Map.of("action", subview.getRemoveSelectedCollectionElementsAction(), "argv", "viewObject=" + viewName)))%>
<% } %>
<%=org.openxava.web.render.ButtonRenderer.render(new org.openxava.web.render.ViewRenderContext(request, response, Map.of("action", subview.getDeleteSelectedCollectionElementsAction(), "argv", "viewObject=" + viewName)))%>
<%
	}
%>
<%
	Iterator itListActions = subview.getActionsNamesList().iterator();
while (itListActions.hasNext()) {
	String listAction = itListActions.next().toString();
%>
<%=org.openxava.web.render.ButtonRenderer.render(new org.openxava.web.render.ViewRenderContext(request, response, Map.of("action", listAction, "argv", "viewObject=" + viewName)))%>
<%
	} // while list actions
	
	Collection<String> listSubcontrollers = subview.getSubcontrollersNamesList();
	for(String listSubcontroller : listSubcontrollers){
%>
<%=org.openxava.web.render.SubButtonRenderer.render(new org.openxava.web.render.ViewRenderContext(request, response, Map.of("controller", listSubcontroller, "argv", "viewObject=" + viewName)))%>
<%
	}
%>

</td></tr>
<%
	}
else {
%>
<td></td>
<%
	String argv = "collectionName=" + collectionName;
	Iterator it = subview.getMetaPropertiesList().iterator();
	String app = request.getParameter("application");
	String module = request.getParameter("module");
	while (it.hasNext()) {
		MetaProperty p = (MetaProperty) it.next(); 
		String propertyKey= propertyPrefix + p.getName();
		String valueKey = propertyKey + ".value";
		request.setAttribute(propertyKey, p);
		request.setAttribute(valueKey, subview.getValue(p.getName()));
		Object value = request.getAttribute(propertyKey + ".value");
		if (WebEditors.mustToFormat(p, view.getViewName())) {
			String fvalue = WebEditors.format(request, p, value, errors, view.getViewName());
			request.setAttribute(propertyKey + ".fvalue", fvalue);
		}
%>
	<td>
		<jsp:include page='<%="/xava/" + WebEditors.getUrl(p, view.getViewName())%>'>
			<jsp:param name="propertyKey" value="<%=propertyKey%>"/>
			<jsp:param name="editable" value="true"/>
		</jsp:include>		
	</td>
	<%
		}
	}
	%>

</tr>
<tr><td>
<%
	try {
%>
	<%
		if (!Is.emptyString(listEditor)) {
	%> 		
		<jsp:include page="<%=listEditor%>">
			<jsp:param name="rowAction" value="<%=lineAction%>"/>	
			<jsp:param name="viewObject" value="<%=viewName%>"/>
		</jsp:include>
	<%
		} else if (collectionView.isCollectionFromModel()) {
	%>
		<%=org.openxava.web.render.CollectionFromModelRenderer.render(new org.openxava.web.render.ViewRenderContext(request, response))%>
	<% } else { %>
		<%=org.openxava.web.render.CollectionListRenderer.render(new org.openxava.web.render.ViewRenderContext(request, response), idCollection, subview, lineAction, viewName, view)%>
	<% } %>
<% } catch (Exception ex) { %>
</td></tr>
<tr><td class='ox-errors'>
<%=ex.getLocalizedMessage()%>
<% } %>
</td></tr>
</table>
<%@ include file="imports.jsp"%>

<%@page import="org.openxava.model.meta.MetaProperty"%>


<%@page import="org.openxava.web.Ids"%><jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<% 
boolean lastSearchKey = "true".equals(request.getParameter("lastSearchKey"));
boolean editable = "true".equals(request.getParameter("editable"));
String viewObject = request.getParameter("viewObject");
viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject;
org.openxava.view.View view = (org.openxava.view.View) context.get(request, viewObject);
String propertyKey = Ids.undecorate(request.getParameter("propertyKey")); 
String propertyName = request.getParameter("propertyName");
MetaProperty p = view.getMetaProperty(propertyName);
if (lastSearchKey) {	
	String referencedModel = p.getMetaModel().getName();
%>
	<% if (view.isSearch() && editable) {%>	
<xava:action action='<%=view.getSearchAction()%>' argv='<%="keyProperty="+propertyKey%>'/>
	<% } %>
	<% if (view.isCreateNew() && editable) {%>
<xava:action action='Reference.createNew' argv='<%="model="+referencedModel + ",keyProperty=" + propertyKey%>'/>
	<% } %>
	<% if (view.isModify() && editable) {%>
<xava:action action='Reference.modify' argv='<%="model="+referencedModel + ",keyProperty=" + propertyKey%>'/>	
	<% } %>
<% 
}
if (editable){
	for (java.util.Iterator itActions = view.getActionsNamesForReference(lastSearchKey).iterator(); itActions.hasNext();) {
		String action = (String) itActions.next();
%>
<xava:action action="<%=action%>"/> 
<%
	}
}

for (java.util.Iterator itActions = view.getActionsNamesForProperty(p, editable).iterator(); itActions.hasNext();) {
	String action = (String) itActions.next();
%>
<xava:action action="<%=action%>" argv='<%="xava.keyProperty="+propertyKey%>'/>
<%
}
%>

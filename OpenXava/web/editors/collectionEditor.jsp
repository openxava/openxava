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
<% if (XavaPreferences.getInstance().isDetailOnBottomInCollections()) { %>
<tr><td>
<% try { %>
	<% if (!Is.emptyString(listEditor)) { %>
		<jsp:include page="<%=listEditor%>">
			<jsp:param name="rowAction" value="<%=lineAction%>"/>	
			<jsp:param name="viewObject" value="<%=viewName%>"/>
		</jsp:include>
	<%
		} else if (collectionView.isCollectionFromModel()) {
	%>
		<%@include file="../collectionFromModel.jsp" %>
	<%
		} else {
	%>
		<%@include file="../collectionList.jsp" %>
	<%
		}
	%>
<%
	} catch (Exception ex) {
%>
</td></tr>
<tr><td class='<%=style.getErrors()%>'>
<%=ex.getLocalizedMessage()%>
<%
	}
%>
</td></tr>
<%
	} // of: if (XavaPreferences...
%>
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
<jsp:include page="../barButton.jsp">
	<jsp:param name="action" value="<%=subview.getAddCollectionElementAction()%>"/>
	<jsp:param name="argv" value='<%="viewObject="+viewName%>'/>
</jsp:include>
<% } %>
<jsp:include page="../barButton.jsp">
	<jsp:param name="action" value="<%=subview.getNewCollectionElementAction()%>"/>
	<jsp:param name="argv" value='<%="viewObject="+viewName%>'/>
</jsp:include>
<jsp:include page="../barButton.jsp">
	<jsp:param name="action" value="<%=subview.getRemoveSelectedCollectionElementsAction()%>"/>
	<jsp:param name="argv" value='<%="viewObject="+viewName%>'/>
</jsp:include>

<%
	}
%>
<%
	Iterator itListActions = subview.getActionsNamesList().iterator();
while (itListActions.hasNext()) {
%>
<jsp:include page="../barButton.jsp">
	<jsp:param name="action" value="<%=itListActions.next().toString()%>"/>
	<jsp:param name="argv" value='<%="viewObject="+viewName%>'/>
</jsp:include>
<%
	} // while list actions
	
	Collection<String> listSubcontrollers = subview.getSubcontrollersNamesList();
	for(String listSubcontroller : listSubcontrollers){
		%>
		<jsp:include page="../subButton.jsp">
			<jsp:param name="controller" value="<%=listSubcontroller%>"/>
			<jsp:param name="argv" value='<%="viewObject="+viewName%>'/>
		</jsp:include>
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
		String script = "";
		if (it.hasNext()) {
	if (subview.throwsPropertyChanged(p)) {			
		script = "onchange='openxava.throwPropertyChanged(\"" + 
				app + "\", \"" + 
				module + "\", \"" +
				propertyKey + "\")'";
	}
		}
		else {
	script = "onblur='openxava.executeAction(\"" + app + "\", \"" + module + "\", \"\", false, \"" + subview.getSaveCollectionElementAction() + "\", \"" + argv + "\")'";
		}
		Object value = request.getAttribute(propertyKey + ".value");
		if (WebEditors.mustToFormat(p, view.getViewName())) {
	String fvalue = WebEditors.format(request, p, value, errors, view.getViewName());
	request.setAttribute(propertyKey + ".fvalue", fvalue);
		}
%>
	<td>
		<jsp:include page="<%=WebEditors.getUrl(p, view.getViewName())%>">
			<jsp:param name="propertyKey" value="<%=propertyKey%>"/>
			<jsp:param name="script" value="<%=script%>"/>
			<jsp:param name="editable" value="true"/>
		</jsp:include>
	</td>
	<%
		}
	}
	%>

</tr>
<%
	if (!XavaPreferences.getInstance().isDetailOnBottomInCollections()) {
%>
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
		<%@include file="../collectionFromModel.jsp" %>
	<% } else { %>
		<%@include file="../collectionList.jsp" %>
	<% } %>
<% } catch (Exception ex) { %>
</td></tr>
<tr><td class='<%=style.getErrors()%>'>
<%=ex.getLocalizedMessage()%>
<% } %>
</td></tr>
<% } // of: if (!XavaPreferences... %>
</table>
<%
String keyPropertyForAction = Ids.undecorate(propertyKey);  

if (editable && view.isCreateNewForReference(ref)) {  
%>
	<xava:action action='Reference.createNew' argv='<%="model="+ref.getReferencedModelName() + ",keyProperty=" + keyPropertyForAction%>'/>
<%
}

if (editable && view.isModifyForReference(ref)) { 
%>
	<xava:action action='Reference.modify' argv='<%="model="+ref.getReferencedModelName() + ",keyProperty=" + keyPropertyForAction%>'/>
<%
}

java.util.Iterator itActions = view.getActionsNamesForReference(ref, editable).iterator(); 
while (itActions.hasNext()) {
	String action = (String) itActions.next();
%>
	<xava:action action="<%=action%>"/>
<%
}
%>

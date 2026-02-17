<%
String keyPropertyForAction = Ids.undecorate(propertyKey);  

if (editable && view.isCreateNewForReference(ref)) {
	String newAction = view.getNewActionForReference(ref);
	if (newAction == null) {
%>
	<xava:action action='Reference.createNew' argv='<%="model="+ref.getReferencedModelName() + ",keyProperty=" + keyPropertyForAction%>'/>
<%
	}
	else {
%>
	<xava:action action='<%=newAction%>' argv='<%="keyProperty=" + keyPropertyForAction%>'/>
<%
	}
}

if (editable && view.isModifyForReference(ref)) {
	String editAction = view.getEditActionForReference(ref);
	if (editAction == null) {
%>
	<xava:action action='Reference.modify' argv='<%="model="+ref.getReferencedModelName() + ",keyProperty=" + keyPropertyForAction%>'/>
<%
	}
	else {
%>
	<xava:action action='<%=editAction%>' argv='<%="keyProperty=" + keyPropertyForAction%>'/>
<%
	}
}
   
if (editable) { 
%>
	<xava:action action='Reference.clear' argv='<%=",keyProperty=" + keyPropertyForAction%>'/>
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

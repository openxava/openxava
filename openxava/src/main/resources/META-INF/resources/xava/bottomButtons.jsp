<%@ include file="imports.jsp"%>

<%@ page import="org.openxava.controller.meta.MetaAction" %>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<%
org.openxava.controller.ModuleManager manager = (org.openxava.controller.ModuleManager) context.get(request, "manager", "org.openxava.controller.ModuleManager");
manager.setSession(session);
if (manager.isBottomButtonsVisible()) { 
	
	boolean buttonBar = !"false".equalsIgnoreCase(request.getParameter("buttonBar"));
	String mode = request.getParameter("xava_mode"); 
	if (mode == null) mode = manager.getModeName(); 
	
	String defaultAction = null; 
	if (org.openxava.util.XavaPreferences.getInstance().isShowDefaultActionInBottom() && manager.isDetailMode()) { 	
		defaultAction = manager.getDefaultActionQualifiedName();
	%>
	<xava:button action="<%=defaultAction%>"/>
	<% 
	} 
	%>
	
	<%
	java.util.Iterator it = manager.getMetaActions().iterator();
	while (it.hasNext()) {
		MetaAction action = (MetaAction) it.next();
		if (!manager.actionApplies(action)) continue; 
		if (action.getQualifiedName().equals(defaultAction)) continue;
		if (action.appliesToMode(mode) && (!buttonBar || !(action.hasImage() || action.hasIcon()))) { 	
			if (action.hasIcon() && action.getLabel().isEmpty()){
					%>
		<xava:image action='<%=action.getQualifiedName()%>'/>
		<%
			} else {
				
		%>
		<xava:button action="<%=action.getQualifiedName()%>"/>
		<%
			}
		}
	}
	%>
	
	<%  
	MetaAction defaultMetaAction = manager.getDefaultMetaAction();
	if (defaultMetaAction != null) {
	%>
	<button class="xava_action" name="xava.DEFAULT_ACTION" type="submit"
		data-application='<%=request.getParameter("application")%>' 
		data-module='<%=request.getParameter("module")%>'
		data-confirm-message="<%=defaultMetaAction.getConfirmMessage(request)%>" 
		data-takes-long="<%=defaultMetaAction.isTakesLong()%>"  
		data-action="<%=manager.getDefaultActionQualifiedName()%>"
		data-in-new-window="<%=defaultMetaAction.inNewWindow()%>" 
	></button>
	<%
	}
	%>	

<% } // if (manager.isBottomButtonsVisible()) { %>
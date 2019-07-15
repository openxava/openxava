<%@ page import="java.util.Map"%>
<%@ page import="java.util.Enumeration"%>
<%@ page import="java.util.StringTokenizer"%>
<%@ page import="org.openxava.web.Ids"%>
<%@ page import="java.awt.event.InputEvent" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="javax.swing.KeyStroke" %>
<%@ page import="org.openxava.controller.meta.MetaAction" %>
<%@ page import="org.openxava.view.View" %>
<%@ page import="org.openxava.util.Is" %>
<%@ page import="org.openxava.util.Users" %>
<%@ page import="org.openxava.util.Locales" %>
<%@ page import="org.openxava.util.XavaResources" %>

<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="messages" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<%
String browser = (String) request.getAttribute("xava.portlet.user-agent");
if (browser == null) { 
	browser = request.getHeader("user-agent");
	request.setAttribute("xava.portlet.user-agent", browser);
}
Locales.setCurrent(request);

org.openxava.controller.ModuleManager manager = (org.openxava.controller.ModuleManager) context.get(request, "manager", "org.openxava.controller.ModuleManager");
manager.setSession(session);

org.openxava.tab.Tab t = (org.openxava.tab.Tab) context.get(request, "xava_tab");
request.setAttribute("tab", t);
%>
<jsp:useBean id="tab" class="org.openxava.tab.Tab" scope="request"/>
<%
View view = (View) context.get(request, "xava_view");
String[] deselected = request.getParameterValues("deselected");
if (deselected != null){
	for (int i = 0; i < deselected.length; i++){
		String d = deselected[i];
		if (d.contains("xava_tab")) tab.friendExecuteJspDeselect(d);
		else if (d.contains("xava_collectionTab")) {
			view.deselectCollection(d);
		}
	}	
}
%>

<% if (!"false".equals(request.getAttribute("xava.sendParametersToTab"))) { %>  
<jsp:setProperty name="tab" property="selected"/>
<% } %>

<%
  
manager.setApplicationName(request.getParameter("application"));
manager.setModuleName(request.getParameter("module"));
boolean loadingModulePage = "true".equals(request.getParameter("loadingModulePage"));
if (!loadingModulePage) manager.executeBeforeEachRequestActions(request, errors, messages); 
view.setRequest(request);
view.setErrors(errors);
view.setMessages(messages);

java.util.Stack previousViews = (java.util.Stack) context.get(request, "xava_previousViews");
for (Iterator it = previousViews.iterator(); it.hasNext(); ) {
	View previousView = (View) it.next();
	previousView.setRequest(request);
	previousView.setErrors(errors);
	previousView.setMessages(messages);	
}

tab.setRequest(request);
tab.setErrors(errors); 
if (manager.isListMode()) {   
	tab.setModelName(manager.getModelName());
	if (tab.getTabName() == null) { 
		tab.setTabName(manager.getTabName());
	}
}
boolean hasProcessRequest = manager.hasProcessRequest(request);
manager.preInitModule(request); 
if (manager.isXavaView(request)) { 
	if (hasProcessRequest) {	
		view.assignValuesToWebView();
	}
}
if (!(loadingModulePage && manager.isCoreViaAJAX(request))) { 
	manager.initModule(request, errors, messages);
	manager.executeOnEachRequestActions(request, errors, messages); 
	if (hasProcessRequest) {
		manager.execute(request, errors, messages);	
		if (manager.isListMode()) { // here and before execute the action
			tab.setModelName(manager.getModelName());	
			if (tab.getTabName() == null) { 
				tab.setTabName(manager.getTabName());
			}
		}
	}
	//after-each-request
	manager.executeAfterEachRequestActions(request, errors, messages);	
}  

if ("true".equals(request.getParameter("firstRequest")) && manager.isCoreViaAJAX(request)) { 
	manager.executeBeforeLoadPage(request, errors, messages);
}
%>

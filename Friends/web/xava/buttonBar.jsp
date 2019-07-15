<%@ include file="imports.jsp"%>

<%@ page import="org.openxava.controller.meta.MetaAction" %>
<%@ page import="org.openxava.util.XavaPreferences"%>
<%@ page import="org.openxava.util.Is"%>
<%@ page import="org.openxava.controller.meta.MetaSubcontroller"%>
<%@ page import="java.util.Collection"%>
<%@ page import="org.openxava.web.Ids"%>
<%@ page import="org.openxava.util.EmailNotifications"%> 
<%@ page import="org.openxava.controller.meta.MetaControllerElement"%>


<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
org.openxava.controller.ModuleManager manager = (org.openxava.controller.ModuleManager) context.get(request, "manager", "org.openxava.controller.ModuleManager");
manager.setSession(session);
boolean onBottom = false;
String mode = request.getParameter("xava_mode");
if (mode == null) mode = manager.isSplitMode()?"detail":manager.getModeName();
boolean headerButtonBar = !manager.isSplitMode() || mode.equals("list");
boolean listFormats = !manager.isSplitMode() && mode.equals("list"); 

if (manager.isButtonBarVisible()) {
%>
	<div class="<%=style.getButtonBar()%>"> 
	<div id="<xava:id name='controllerElement'/>">
	<span style="float: left">
	<%
	java.util.Stack previousViews = (java.util.Stack) context.get(request, "xava_previousViews");
	if (manager.isDetailMode() && !manager.isDetailModeOnly() && previousViews.isEmpty()) { 
	%>
		<jsp:include page="barButton.jsp">
			<jsp:param name="action" value="<%=manager.getGoListAction()%>"/>
		</jsp:include>
	<% 
	} 

	Collection<MetaControllerElement> elements = manager.getMetaControllerElements(); 
	for (MetaControllerElement element : elements){
		if (!element.appliesToMode(mode)) continue;
		if (element instanceof MetaAction){
			MetaAction action = (MetaAction) element;
			if (!manager.actionApplies(action)) continue; 
			if (action.hasImage() || action.hasIcon()) {	
			%>
			<jsp:include page="barButton.jsp">
				<jsp:param name="action" value="<%=action.getQualifiedName()%>"/>
			</jsp:include>		
			<%
			}
		}
		else if (element instanceof MetaSubcontroller){
			MetaSubcontroller subcontroller = (MetaSubcontroller) element;
			if (subcontroller.hasActionsInThisMode(mode)){
			%>
			<jsp:include page="subButton.jsp">
				<jsp:param name="controller" value="<%=subcontroller.getControllerName()%>"/>
				<jsp:param name="image" value="<%=subcontroller.getImage()%>"/>
				<jsp:param name="icon" value="<%=subcontroller.getIcon()%>"/>
			</jsp:include>
			<%
			}
		}
	}
	%>
	</span>
	</div>

	<div id="<xava:id name='modes'/>">
	<span style="float: right">
	<span style="float: left;" class="<%=style.getListFormats()%>">
	<%
	if (listFormats) { 	
		String tabObject = request.getParameter("tabObject");
		tabObject = (tabObject == null || tabObject.equals(""))?"xava_tab":tabObject;
		org.openxava.tab.Tab tab = (org.openxava.tab.Tab) context.get(request, tabObject);
		Collection<String> editors = org.openxava.web.WebEditors.getEditors(tab.getMetaTab());
		if (editors.size() > 1) for (String editor: editors) {
			String icon = org.openxava.web.WebEditors.getIcon(editor);
			if (icon == null) continue; 
			String selected = editor.equals(tab.getEditor())?style.getSelectedListFormat():"";
			if (Is.emptyString(editor)) editor = "__NONAME__"; 
	%>
	<xava:link action="ListFormat.select" argv='<%="editor=" + editor%>' cssClass="<%=selected%>">
		<i class="mdi mdi-<%=icon%>" onclick="openxava.onSelectListFormat(event)" title="<%=org.openxava.util.Labels.get(editor)%>"></i>
	</xava:link>			
	<%				
		}
	}	
	%>
	</span>
		
	<%
	if (EmailNotifications.isEnabled(manager.getModuleName())) {
		if (EmailNotifications.isSubscribedCurrentUserToModule(manager.getModuleName())) {
	%>
			<span class="<%=style.getSubscribed()%>"> 
				<xava:image action='EmailNotifications.unsubscribe'/>
			</span>
	<%  
		}
		else {
	%>	
			<span class="<%=style.getUnsubscribed()%>"> 
				<xava:image action='EmailNotifications.subscribe'/>
			</span> 	
	<%
		}
	}

	
	if (XavaPreferences.getInstance().isHelpAvailable() && style.isHelpAvailable()) {
		String language = request.getLocale().getLanguage();
		String href = XavaPreferences.getInstance().getHelpPrefix(); 
		String suffix = XavaPreferences.getInstance().getHelpSuffix(); 
		String target = XavaPreferences.getInstance().isHelpInNewWindow() ? "_blank" : "";
		if (href.startsWith("http:") || href.startsWith("https:")) {
			if (href.endsWith("_")) href = href + language;
			if (!Is.emptyString(suffix)) href = href + suffix;
		}
		else {
			href = 
				"/" + manager.getApplicationName() + "/" + 
				href +
				manager.getModuleName() +
				"_" + language + 
				suffix;
		}
		String helpImage = null;
		if (style.getHelpImage() != null) helpImage = !style.getHelpImage().startsWith("/")?request.getContextPath() + "/" + style.getHelpImage():style.getHelpImage();
	%>
		<span class="<%=style.getHelp()%>">  
			<a href="<%=href%>" target="<%=target%>">
				<% if (helpImage == null) { %>
				<i class="mdi mdi-help-circle"></i>
				<% } else { %>
				<a href="<%=href%>" target="<%=target%>"><img src="<%=helpImage%>"/></a>
				<% } %>
			</a>
		</span>
	<%
	}
	%>
	&nbsp;
	</span>		
	</div>	<!-- modes -->
	</div>
	
<% } // end isButtonBarVisible %>
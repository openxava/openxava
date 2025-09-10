<% Servlets.setCharacterEncoding(request, response); %> <%-- Must be the very first, in order character encoding takes effect --%>

<%@ include file="imports.jsp"%>

<%@page import="java.io.File"%>
<%@page import="java.util.Arrays"%>
<%@page import="org.openxava.util.XavaResources"%>
<%@page import="org.openxava.util.Locales"%>
<%@page import="org.openxava.util.Users"%>
<%@page import="org.openxava.util.XSystem"%>
<%@page import="org.openxava.util.Strings"%>
<%@page import="org.openxava.util.Is"%>
<%@page import="org.openxava.util.XavaPreferences"%> 
<%@page import="org.openxava.web.dwr.Module"%>
<%@page import="org.openxava.web.servlets.Servlets"%>
<%@page import="org.openxava.web.Ids"%>
<%@page import="org.openxava.web.Requests"%>
<%@page import="org.openxava.web.EditorsResources"%> 
<%@page import="org.openxava.web.style.Themes"%>  
<%@page import="org.apache.commons.logging.LogFactory" %>
<%@page import="org.apache.commons.logging.Log" %>
<%@page import="org.openxava.web.Browsers"%> 


<%!private static Log log = LogFactory.getLog("module.jsp");

	private String getAdditionalParameters(HttpServletRequest request) {
		StringBuffer result = new StringBuffer();
		for (java.util.Enumeration en = request.getParameterNames(); en
				.hasMoreElements();) {
			String name = (String) en.nextElement();
			if ("application".equals(name) || "module".equals(name)) continue;
			if (!Strings.isJavaIdentifier(name)) continue;	
			String value = request.getParameter(name);
			if (!Is.emptyString(value) && !(value.contains("<") || value.contains("\""))) { // If change pass the ZAP test again
				result.append('&');
				result.append(name);
				result.append('=');
				result.append(value);
			} 
		}
		return result.toString();
	}%>

<%
	request.setAttribute("style", org.openxava.web.style.Style.getInstance());
%>

<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="messages" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>
<%
	String windowId = context.getWindowId(request);
	context.setCurrentWindowId(windowId);		
	request.getSession().setAttribute("xava.user",
			request.getRemoteUser());
	String app = request.getParameter("application");
	String module = context.getCurrentModule(request);
	String contextPath = (String) request.getAttribute("xava.contextPath");
	if (contextPath == null) contextPath = request.getContextPath();
	
	org.openxava.controller.ModuleManager managerHome = (org.openxava.controller.ModuleManager) context
			.get(request, "manager",
					"org.openxava.controller.ModuleManager");
	org.openxava.controller.ModuleManager manager = (org.openxava.controller.ModuleManager) context
			.get(app, module, "manager",
					"org.openxava.controller.ModuleManager");

	manager.setSession(session);
	managerHome.setSession(session); 
	manager.setApplicationName(request.getParameter("application"));

	manager.setModuleName(module); // In order to show the correct description in head
	
	boolean restoreLastMessage = false;
	if (manager.isFormUpload()) {
		new Module().requestMultipart(request, response, app, module);
	}
	else {
		restoreLastMessage = true;
	}	

	boolean htmlHead = !Is.equalAsStringIgnoreCase(request.getParameter("htmlHead"), "false");
	String version = org.openxava.controller.ModuleManager.getVersion();
	String realPath = request.getSession().getServletContext()
			.getRealPath("/");
	Requests.init(request, app, module);
	manager.log(request, "MODULE:" + module);
	manager.setModuleURL(request);
%>
<jsp:include page="execute.jsp">
	<jsp:param name="loadingModulePage" value="true"/> 
</jsp:include>
<%
	if (htmlHead) {	
%>
 
<!DOCTYPE html>

<head>
	<title><%=managerHome.getModuleDescription()%></title>
	
	<%=style.getMetaTags()%>
	
<%
	}

	if (style.getCssFile() != null) {
%>
	<link href="<%=contextPath%>/xava/style/<%=style.getCssFile()%>?ox=<%=version%>" rel="stylesheet" type="text/css">
<%
	}
%>
	<link rel="stylesheet" type="text/css" media="all" href="<%=contextPath%>/xava/style/custom.css?ox=<%=version%>"/>

	<%
	for (String styleEditor: EditorsResources.listCSSFiles(realPath)) {
	%>
	<link href="<%=contextPath%>/xava/editors/<%=styleEditor%>?ox=<%=version%>" rel="stylesheet" type="text/css">
	<%
	}
	%>	

	<% if (request.getAttribute("xava.dwr.loaded") == null) { %>
	<script type='text/javascript' <xava:nonce/>> 
		if (typeof dwr == 'undefined') dwr = {};
		if (!dwr.engine) dwr.engine = {};
		dwr.engine._contextPath = "<%=contextPath%>";
	</script>
	<script type='text/javascript' src='<%=contextPath%>/xava/js/dwr-engine.js?ox=<%=version%>'></script>	
	<% } %>
	<script type='text/javascript' src='<%=contextPath%>/dwr/util.js?ox=<%=version%>'></script>
	<script type='text/javascript' src='<%=contextPath%>/dwr/interface/Module.js?ox=<%=version%>'></script>
	<script type='text/javascript' src='<%=contextPath%>/dwr/interface/Tab.js?ox=<%=version%>'></script>
	<script type='text/javascript' src='<%=contextPath%>/dwr/interface/View.js?ox=<%=version%>'></script>
	<script type='text/javascript' src='<%=contextPath%>/xava/js/openxava.js?ox=<%=version%>'></script>
	<script type='text/javascript' <xava:nonce/>> 
		openxava.lastApplication='<%=app%>'; 		
		openxava.lastModule='<%=module%>'; 
		openxava.language='<%=Locales.getCurrent().getLanguage()%>'; 
		openxava.contextPath = '<%=contextPath%>';
	</script>
	<%
		if (new File(realPath + "/xava/js/custom-editors.js").exists()) {
	%>
	<script type='text/javascript' src='<%=contextPath%>/xava/js/custom-editors.js?ox=<%=version%>'></script>
	<%
		log.warn(XavaResources.getString("custom_editors_deprecated"));
		}
	%>		  
	<script type="text/javascript" src="<%=contextPath%>/xava/js/jquery.js?ox=<%=version%>"></script>	 
	<script type="text/javascript" src="<%=contextPath%>/xava/js/jquery-ui.js?ox=<%=version%>"></script>
	<script type="text/javascript" src="<%=contextPath%>/xava/js/jquery.sorttable.js?ox=<%=version%>"></script>	
	<script type="text/javascript" src="<%=contextPath%>/xava/js/jquery.ui.touch-punch.js?ox=<%=version%>"></script>
	<script type='text/javascript' src='<%=contextPath%>/xava/js/typewatch.js?ox=<%=version%>'></script>

	<%
	String browser = request.getHeader("user-agent"); 
	boolean browserIsHtmlUnit = browser != null && browser.contains("HtmlUnit");
	for (String editorJS: EditorsResources.listJSFiles(realPath)) {
		if (browserIsHtmlUnit && editorJS.equals("js/tinymce.js")) continue; // Ad hoc, we should move this outside OpenXava core, in a file or following convention
        String encoding = editorJS.toLowerCase().endsWith("-utf8.js") ? "UTF-8" : "ISO-8859-1"; 
    %>
	<script type="text/javascript" charset="<%=encoding%>" src="<%=contextPath%>/xava/editors/<%=editorJS%>?ox=<%=version%>"></script>
	<%
	}
	
	String[] jsFiles = request.getParameterValues("jsFiles");
	if (jsFiles != null) {
		for (int i = 0; i < jsFiles.length; i++) {
			if (jsFiles[i].endsWith(".js")) {
	%>
	<script type="text/javascript" src="<%=contextPath%>/<%=jsFiles[i]%>?ox=<%=version%>"></script>				
	<%		}
		}
	}	
	%>	
<%
	if (htmlHead) { 	
%>
</head> 
<body bgcolor="#ffffff">
<%
	}
%> 
<% 
boolean coreViaAJAX = manager.isCoreViaAJAX(request);
if (!coreViaAJAX && restoreLastMessage) {
	Module.restoreLastMessages(request, app, module);
}	

if (manager.isResetFormPostNeeded()) {
%>		
	<form id="xava_reset_form">
		<% if (!"true".equals(request.getParameter("friendlyURL"))) { // To support old URL style (with xava/moduls.jsp?application=...) %>
		<input name="application" type="hidden" value="<%=request.getParameter("application")%>"/>
		<input name="module" type="hidden" value="<%=request.getParameter("module")%>"/>
		<% } %>
	</form>
<% } else  { %>
	<% if (!coreViaAJAX) manager.executeBeforeLoadPage(request, errors, messages); %>
	<input id="xava_last_module_change" type="hidden" value=""/>
	<input id="xava_window_id" type="hidden" value="<%=windowId%>"/>	
	<input id="<xava:id name='loading'/>" type="hidden" value="<%=coreViaAJAX%>"/>
	<input id="<xava:id name='loaded_parts'/>" type="hidden" value=""/>
	<input id="<xava:id name='view_member'/>" type="hidden" value=""/>
			
	<%-- Layer for progress bar --%>
	<div id='xava_processing_layer'>
		<div><%=XavaResources.getString(request, "processing")%></div>
		<i class="mdi mdi-cog spin"></i>
	</div>	
	<%=style.getCoreStartDecoration()%>
	<div id="<xava:id name='core'/>" class="ox-module">
		<%			
			if (!coreViaAJAX) {
		%>
		<jsp:include page="core.jsp"/>
		<%
			}
		%>		
	</div>
	<%=style.getCoreEndDecoration()%>
	
<% } %>			
	<% if (Themes.isChooserEnabled(request)) { %>
	<jsp:include page="themeChooser.jsp"/>
	<% } %>
	<div id="xava_console" >
	</div>
	<div id="xava_loading">				
		<i class="mdi mdi-autorenew module-loading spin"></i>
		&nbsp;<xava:message key="loading"/>...		 
	</div>
	<% if (!style.isFixedPositionSupported()) { %>
	<div id="xava_loading2">
		<i class="mdi mdi-autorenew module-loading spin"></i>
		&nbsp;<xava:message key="loading"/>...
	</div>	
	<% } %>	
<%
	if (htmlHead) { 	
%>
</body>
</html>
<%
	}
%>

<% 
if (manager.isResetFormPostNeeded()) {  
	manager.setResetFormPostNeeded(false);		
%>		
	<script type="text/javascript" <xava:nonce/>>
	$("#xava_reset_form").submit();
	</script>		
<% } else  { 		
%>

<span id='<xava:id name="postjs"/>'>
</span>

<script type="text/javascript" <xava:nonce/>> 
<%String prefix = Strings.change(manager.getApplicationName(), "-",
					"_")
					+ "_" + Strings.change(manager.getModuleName(), "-", "_");
			String onLoadFunction = prefix + "_openxavaOnLoad";
			String initiated = prefix + "_initiated";%>
<%=onLoadFunction%> = function() {
	document.additionalParameters="<%=getAdditionalParameters(request)%>"; 
	if (openxava != null && openxava.<%=initiated%> == null) {
		openxava.browser.ie = <%=Browsers.isIE(request)%>;
		openxava.browser.ff = <%=Browsers.isFF(request)%>;
		openxava.browser.edge = <%=Browsers.isEdge(request)%>; 
		openxava.showFiltersMessage = '<xava:message key="show_filters"/>';
		openxava.hideFiltersMessage = '<xava:message key="hide_filters"/>';
		openxava.confirmLoseChangesMessage = '<xava:message key="confirm_lose_changes"/>';
		openxava.confirmRemoveFileMessage = '<xava:message key="confirm_remove_file"/>';   
		openxava.selectedRowClass = '<%=style.getSelectedRow()%>';
		openxava.currentRowClass = '<%=style.getCurrentRow()%>';
		openxava.currentRowCellClass = '<%=style.getCurrentRowCell()%>';
		openxava.selectedListFormatClass = '<%=style.getSelectedListFormat()%>'; 
		openxava.customizeControlsClass = '<%=style.getCustomizeControls()%>';
		openxava.errorEditorClass = '<%=style.getErrorEditor()%>';
		openxava.editorClass = '<%=style.getEditor()%>'; 
		openxava.listAdjustment = <%=style.getListAdjustment()%>;
		openxava.collectionAdjustment = <%=style.getCollectionAdjustment()%>;
		openxava.closeDialogOnEscape = <%=browser != null && browser.indexOf("Firefox") >= 0 ? "false":"true"%>;		  
		openxava.calendarAlign = '<%=browser != null && browser.indexOf("MSIE 6") >= 0 ? "tr":"Br"%>';
		openxava.subcontrollerSelectedClass = '<%=style.getSubcontrollerSelected()%>';
		openxava.mapsTileProvider = '<%=XavaPreferences.getInstance().getMapsTileProvider()%>';
		openxava.mapsAttribution = "<%=XavaPreferences.getInstance().getMapsAttribution().replace("\"", "'")%>";
		openxava.mapsTileSize = <%=XavaPreferences.getInstance().getMapsTileSize()%>;
		openxava.mapsZoomOffset = <%=XavaPreferences.getInstance().getMapsZoomOffset()%>;
		openxava.filterOnChange = <%=XavaPreferences.getInstance().isFilterOnChange()%>;
		<% java.text.DecimalFormatSymbols symbols = java.text.DecimalFormatSymbols.getInstance(Locales.getCurrent()); %>
		openxava.decimalSeparator = "<%=symbols.getDecimalSeparator()%>";
		openxava.groupingSeparator = "<%=symbols.getGroupingSeparator()%>";		
		openxava.setHtml = <%=style.getSetHtmlFunction()%>;			
		<% if (XavaPreferences.getInstance().isEnterMovesToNextField()) { %>
		openxava.initFocusKey = openxava.setEnterAsFocusKey;
		<% } %>
		<% if (browserIsHtmlUnit) { // Because of low performance of fadeIn with HtmlUnit %>  
		openxava.fadeIn = openxava.show;
		openxava.browser.htmlUnit = true; 
		<% } %>
		<%String initThemeScript = style.getInitThemeScript();
			if (initThemeScript != null) {%>
		openxava.initTheme = function () { <%=style.getInitThemeScript()%> }; 
		<%}%>
		<%if (coreViaAJAX) {%>
		openxava.init("<%=manager.getApplicationName()%>", "<%=manager.getModuleName()%>", false);
		openxava.ajaxRequest("<%=manager.getApplicationName()%>", "<%=manager.getModuleName()%>", true);	
		<%} else {%>
		openxava.init("<%=manager.getApplicationName()%>", "<%=manager.getModuleName()%>", true);
		openxava.setFocus("<%=manager.getApplicationName()%>", "<%=manager.getModuleName()%>"); 
		<%}%>
		openxava.<%=initiated%> = true;
	}
}
<%=onLoadFunction%>();
</script>
<% }
try {
	manager.commit();
}
finally {
	context.cleanCurrentWindowId(); 
	org.openxava.util.SessionData.clean();
}
%>

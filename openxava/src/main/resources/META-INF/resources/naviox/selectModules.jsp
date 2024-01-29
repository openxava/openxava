<%--
NaviOX. Navigation and security for OpenXava applications.
Copyright 2014 Javier Paniza Lucas

License: Apache License, Version 2.0.	
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

License: GNU Lesser General Public License (LGPL), version 2.1 or later.
See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
--%>

<%@include file="../xava/imports.jsp"%>

<%@page import="java.util.Iterator"%>
<%@page import="java.util.Collection"%>
<%@page import="org.openxava.util.Is"%>
<%@page import="org.openxava.util.Strings"%> 
<%@page import="org.openxava.util.Locales"%> 
<%@page import="org.openxava.application.meta.MetaModule"%>

<jsp:useBean id="modules" class="com.openxava.naviox.Modules" scope="session"/>

<%
String searchWord = request.getParameter("searchWord");
searchWord = searchWord == null?"":Strings.removeAccents(searchWord.toLowerCase()); 
Collection modulesList = modules.getMenuModules(request);
boolean bookmarkModules = "true".equals(request.getParameter("bookmarkModules"));
String smodulesLimit = request.getParameter("modulesLimit");
int modulesLimit = smodulesLimit == null?30:Integer.parseInt(smodulesLimit); 
int counter = 0; 
boolean loadMore = false;
for (Iterator it= modulesList.iterator(); it.hasNext();) {
	if (counter == modulesLimit) {
		loadMore = true; 
		break;
	}
	MetaModule module = (MetaModule) it.next();
	String selected = module.getName().equals(modules.getCurrent(request))?"selected":"";
	String label = module.getLabel(Locales.getCurrent()); 
	String description = module.getDescription(Locales.getCurrent());
	String normalizedLabel = Strings.removeAccents(label.toLowerCase()); 
	String normalizedDescription = Strings.removeAccents(description.toLowerCase());
	if (!Is.emptyString(searchWord) && !normalizedLabel.contains(searchWord) && !normalizedDescription.contains(searchWord)) continue;
	counter++;
%>
	<a href="<%=modules.getModuleURI(request, module)%>?init=true" title="<%=description%>"> <%-- href with the URL in order right mouse button works to add in another tab --%>
	<div id="<%=module.getName()%>_module" class="module-row <%=selected%>">
		<div class="module-name">
			<%=label%>
			<% if (bookmarkModules) { %>
			<i class="mdi mdi-star bookmark-decoration"></i>
			<% } %>
			<i id="<%=module.getName()%>_loading" class="mdi mdi-autorenew module-loading spin"></i>
		</div>
	</div>	
	</a>
	
<%	
}

if (loadMore) {
%>
	<a id="display_all_modules" data-search-word="<%=searchWord%>">
	<div id="more_modules" class="module-row">
	<span id="loading_more_modules">
	<xava:message key="loading"/>...
	<i class="mdi mdi-autorenew module-loading spin"></i>
	</span>
	<span id="load_more_modules">
	<xava:message key="load_more_modules"/>...
	</span>	
	</div>	
	</a>
<%
}
%>
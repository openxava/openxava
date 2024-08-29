<%@ include file="../imports.jsp"%>

<%@ page import="org.openxava.util.Is" %>
<%@ page import="org.openxava.controller.ModuleManager" %>
<%@ page import="org.openxava.web.editors.Card" %>
<%@ page import="org.openxava.web.editors.CardIterator" %>
<%@ page import="org.openxava.tab.impl.IXTableModel" %>
<%@ page import="org.openxava.tab.Tab"%>
<%@ page import="org.openxava.view.View"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>

<div id="<xava:id name='list'/>" class="ox-cards"> 
<%
ModuleManager manager = (ModuleManager) context.get(request, "manager", "org.openxava.controller.ModuleManager");
Tab tab = (Tab) context.get(request, "xava_tab");
View view = (View) context.get(request, "xava_view");
String action=request.getParameter("rowAction");
action=action==null?manager.getEnvironment().getValue("XAVA_LIST_ACTION"):action;
boolean loadMore = false;
int limit = tab.getTableModel().getRowCount() - 1;
if (tab.getTableModel().isAllLoaded()) limit = Integer.MAX_VALUE - 2;
int chunkSize = tab.getTableModel().getChunkSize(); 
if (limit % chunkSize != 0) limit += 2;   
if (limit < chunkSize) limit = chunkSize; 
int i = 0;
for (Card card: new CardIterator(tab, view, request, errors)) {
%>	
	<div class="ox-card<%=card.getStyle()%>" data-action="<%=action%>" data-row="<%=i++%>">
		<div class="ox-card-header"><%=card.getHeader()%></div>
		<div class="ox-card-subheader"><%=card.getSubheader()%></div>
		<div class="ox-card-content"><%=card.getContent()%></div>
	</div>	
<%
	if (i >= limit) {
		loadMore = true;
		break;
	}
}

if (loadMore) {
%>
<div id="xava_loading_more_elements" class="ox-card">
	<i class="mdi mdi-autorenew spin"></i>
	<xava:message key="loading"/>...
</div>
<script <xava:nonce/>>
if ($("#xava_loading_more_elements").is(":visible")) {
	$(window).scroll(function () {
		if ($("#xava_loading_more_elements").is(":visible")) {
			if($(document).height() <= $(window).scrollTop() + $(window).height() + 1) { 		
				openxava.executeAction('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>', false, false, 'Cards.loadMoreCards'); 
			}
		}
	});	
}
</script>
<%
}
else if (i == 0) {
%>
<div class="ox-no-objects"><xava:message key="no_objects"/></div>
<%
}
%>

</div>


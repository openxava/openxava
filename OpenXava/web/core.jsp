<%@ include file="imports.jsp"%>

<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="messages" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>
<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<%
org.openxava.controller.ModuleManager manager = (org.openxava.controller.ModuleManager) context.get(request, "manager");
org.openxava.view.View view = (org.openxava.view.View) context.get(request, "xava_view");
boolean buttonBar = !"false".equalsIgnoreCase(request.getParameter("buttonBar")); 
String focusPropertyId = manager.isListMode()?org.openxava.web.Lists.FOCUS_PROPERTY_ID:view.getFocusPropertyId();
%>
<form id="<xava:id name='form'/>" name="<xava:id name='form'/>"
	method='POST' <%=manager.getEnctype()%> 
	<%=manager.getFormAction(request)%> style="display: inline;"
	onsubmit="return false">
	
<%-- Here, and not at bottom of form, because if there are some erroneous
markup inside the view, then maybe these hidden fields are not found by javascript. 
Concretely, if you put this hidden fields on bottom then InvoiceAmounts (from OpenXavaTest) 
with Firefox 3 and Liferay 5.1.1, 5.1.2 and 5.2.2 produces a JavaScript error.
--%>	
<INPUT type="hidden" name="<xava:id name='xava_action'/>" value=""/>
<INPUT type="hidden" name="<xava:id name='xava_action_argv'/>" value=""/>
<INPUT type="hidden" name="<xava:id name='xava_action_range'/>" value=""/>
<INPUT type="hidden" name="<xava:id name='xava_action_already_processed'/>" value=""/>
<INPUT type="hidden" name="<xava:id name='xava_action_application'/>" value="<%=request.getParameter("application")%>"/>
<INPUT type="hidden" name="<xava:id name='xava_action_module'/>" value="<%=request.getParameter("module")%>"/>
<INPUT type="hidden" name="<xava:id name='xava_changed_property'/>"/>
<INPUT type="hidden" id="<xava:id name='xava_current_focus'/>" 
	name="<xava:id name='xava_current_focus'/>"/>
<INPUT type="hidden" id="<xava:id name='xava_previous_focus'/>" 
	name="<xava:id name='xava_previous_focus'/>"/>
<INPUT type="hidden" name="<xava:id name='xava_focus_forward'/>"/> 
<INPUT type="hidden" id="<xava:id name='xava_focus_property_id'/>" 
	name="<xava:id name='xava_focus_property_id'/>" value="<%=focusPropertyId%>"/>
	
<%
String listModeClass=manager.isListMode()?"class='" + style.getListMode() + "'":""; 
%>

<div <%=listModeClass%> <%=style.getModuleSpacing()%>>

	<% if (buttonBar) {	%> 
    <div id='<xava:id name="button_bar"/>'>    
		<jsp:include page="buttonBar.jsp"/> 
	</div>
	<% } %>
	    
    
    <div class="<%=style.getView()%>">
    <% if (style.isShowModuleDescription() && !manager.isListMode()) { %>
		<div class="<%=style.getModuleDescription()%>"> 
		<%=manager.getModuleDescription()%>
		</div>
	<% } %>    
    
    	<div id='<xava:id name="errors"/>' style="display: inline;"> 
    		<jsp:include page="errors.jsp"/>
		</div>
    
		<div id='<xava:id name="messages"/>' style="display: inline;"> 
			<jsp:include page="messages.jsp"/>
		</div>            

    	<div id='<xava:id name="view"/>' <%=manager.isListMode()?"":("class='" + style.getDetail() + (view.isSimple()?" ox-simple-layout":"") + (view.isFlowLayout()?" ox-flow-layout":"") +  "'")%> style='padding-top: 2px;'>
			<jsp:include page='<%=manager.getViewURL()%>'/>		
		</div>    	
		
		<%@include file="viewExt.jsp"%>
	
	</div>

	<% if (style.isSeparatorBeforeBottomButtons()) { %>
	<div style="clear: both; padding-top: 2px;"></div>
	<% } %>

    <div id='<xava:id name="bottom_buttons"/>' class="<%=style.getBottomButtons()%>" style="<%=style.getBottomButtonsStyle()%>">
		<jsp:include page="bottomButtons.jsp"/>
	</div>
    
</div>
 
</form>

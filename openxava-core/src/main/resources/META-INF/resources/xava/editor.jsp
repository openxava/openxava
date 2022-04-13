<%@ include file="imports.jsp"%>

<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%@ page import="org.openxava.util.XavaPreferences" %>
<%@ page import="org.openxava.util.Is" %>
<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.view.meta.MetaPropertyView" %>


<%
String viewObject = request.getParameter("viewObject");
viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject;
org.openxava.view.View view = (org.openxava.view.View) context.get(request, viewObject);
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String shasFrame = request.getParameter("hasFrame"); 
boolean hasFrame="true".equals(shasFrame)?true:false;
boolean editable = view.isEditable(p);
boolean lastSearchKey = view.isLastSearchKey(p); 
boolean throwPropertyChanged = view.throwsPropertyChanged(p);
	

int labelFormat = view.getLabelFormatForProperty(p);
String labelStyle = view.getLabelStyleForProperty(p);
if (Is.empty(labelStyle)) labelStyle = XavaPreferences.getInstance().getDefaultLabelStyle();
String label = view.getLabelFor(p);
%>

<% if (view.isFlowLayout()) { %> 
<div>  
<% } %>

<%@ include file="htmlTagsEditor.jsp"%>
<%  
if (first && !view.isAlignedByColumns()) label = org.openxava.util.Strings.change(label, " ", "&nbsp;");
%>

<% if (!hasFrame) {  %>

<%=preLabel%>
<% 
if (labelFormat == MetaPropertyView.NORMAL_LABEL) {
%>
<span id="<xava:id name='<%="label_" + view.getPropertyPrefix() + p.getName()%>'/>" class="<%=labelStyle%>">
<%=label%>
</span>
<% } %>
<%=postLabel%>
<%=preEditor%>
<% 
if (labelFormat == MetaPropertyView.SMALL_LABEL) { 
%>
<span id="<xava:id name='<%="label_" + view.getPropertyPrefix() + p.getName()%>'/>" class="<%=style.getSmallLabel()%> <%=labelStyle%>"><%=label%></span><br/> 
<% } %>
<% } // if (!hasFrame)
String placeholder = !Is.empty(p.getPlaceholder()) ? "data-placeholder='" + p.getPlaceholder() + "'" : "";
String required = view.isEditable() && p.isRequired() ? style.getRequiredEditor():""; 
%>
<span id="<xava:id name='<%="editor_" + view.getPropertyPrefix() + p.getName()%>'/>" class="xava_editor <%=required%>" <%=placeholder%>>
<xava:editor property="<%=p.getName()%>" editable="<%=editable%>" throwPropertyChanged="<%=throwPropertyChanged%>"/>
</span>

<% if (!(lastSearchKey && view.displayWithFrame())) { %> 
	<span id="<xava:id name='<%="property_actions_" + view.getPropertyPrefix() + p.getName()%>'/>">
		<% if (view.propertyHasActions(p)) { %>
			<jsp:include page="propertyActions.jsp">
				<jsp:param name="propertyName" value="<%=p.getName()%>"/>
				<jsp:param name="lastSearchKey" value="<%=lastSearchKey%>"/>
				<jsp:param name="editable" value="<%=editable%>"/>
			</jsp:include>
		<% } %>
	</span>
<% } %> 

<% if (!hasFrame) { %>
<%@ include file="propertyActionsExt.jsp"%> 
<%=postEditor%>
<% if (labelFormat == MetaPropertyView.SMALL_LABEL) { %>
<% } %>

<% } // if (!hasFrame) %>

<% if (view.isFlowLayout()) { %> 
</div>  
<% } %>

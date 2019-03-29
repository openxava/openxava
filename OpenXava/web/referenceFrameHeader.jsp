<%@ include file="imports.jsp"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%@ page import="org.openxava.web.WebEditors"%>
<%@ page import="org.openxava.view.View" %>
<%@ page import="org.openxava.tab.Tab" %>
<%@ page import="org.openxava.util.XavaPreferences" %>
<%@ page import="org.openxava.util.XavaResources" %>
<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.web.Ids" %>
<%@ page import="org.openxava.web.Collections" %>

<%
String referenceName = request.getParameter("referenceName");
String viewObject = request.getParameter("viewObject");
View view = (View) context.get(request, viewObject);
View referenceView = view.getSubview(referenceName);
boolean editable = referenceView.isKeyEditable();
String propertyKey = referenceView.getPropertyPrefix() + referenceView.getSearchKeyName(); 		
%>

<span id="<xava:id name='<%="property_actions_" + propertyKey%>'/>">
	<jsp:include page="propertyActions.jsp">
		<jsp:param name="propertyKey" value="<%=propertyKey%>"/>
		<jsp:param name="propertyName" value="<%=referenceView.getSearchKeyName()%>"/>
		<jsp:param name="lastSearchKey" value="true"/>
		<jsp:param name="editable" value="<%=editable%>"/>
		<jsp:param name="viewObject" value="<%=referenceView.getViewObject()%>"/>
		<jsp:param name="referenceActions" value="true"/> 
		<jsp:param name="propertyActions" value="false"/> 
	</jsp:include>
</span>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%@ page import="org.openxava.util.KeyAndDescription" %>
<%@ page import="org.openxava.util.Is" %>
<%@ page import="org.openxava.util.XavaResources" %>
<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.calculators.DescriptionsCalculator" %>
<%@ page import="org.openxava.formatters.IFormatter" %>
<%@ page import="org.openxava.filters.IFilter" %>
<%@ page import="org.openxava.filters.IRequestFilter" %>
<%@ page import="org.openxava.mapping.PropertyMapping"%>
<%@ page import="org.openxava.converters.IConverter"%>
<%@ page import="org.openxava.session.MyReport"%>
<%@ page import="org.openxava.util.Labels"%>

<%
String viewObject = request.getParameter("viewObject");
viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject;
org.openxava.view.View view = (org.openxava.view.View) context.get(request, viewObject);
String propertyKey = request.getParameter("propertyKey");
String script = request.getParameter("script");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String title = (p == null)?"":p.getDescription(request);
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
boolean editable = "true".equals(request.getParameter("editable"));
boolean label = org.openxava.util.XavaPreferences.getInstance().isReadOnlyAsLabel() || "true".equalsIgnoreCase(request.getParameter("readOnlyAsLabel"));
org.openxava.session.MyReport report = (org.openxava.session.MyReport) view.getModel();

String[] sharedDescriptions = report.getAllNamesSharedUser();
String[] currentUserDescription = report.getAllNamesCurrentUser();
String suffix = Labels.get("sharedReportSuffix");
if (!editable) {
%>
<select id="<%=propertyKey%>" name="<%=propertyKey%>" tabindex="1" class=<%=style.getEditor()%> <%=script%> title="<%=title%>">	
<%	
	// current user
	for (int i=0; i<currentUserDescription.length; i++) {
		String selected = "";
		String description = currentUserDescription[i];
		if (Is.equalAsStringIgnoreCase(fvalue, description)) {
			selected = "selected"; 
		} 		
	%>
		<option value="<%=description%>" <%=selected%>><%=description%></option>
	<%
	}
	
	// shared reports
	for (int i=0; i<sharedDescriptions.length; i++){
		String selected = "";
		String description = sharedDescriptions[i];
		String descriptionKey = description + MyReport.SHARED_REPORT;
		if (Is.equalAsStringIgnoreCase(fvalue, descriptionKey)) {
			selected = "selected"; 
		}
	%>
		<option value="<%=descriptionKey%>" <%=selected%>><%=description%> <%=suffix%></option>
	<%
	} 
%>
</select>
<input type="hidden" name="<%=propertyKey%>__DESCRIPTION__" value="<%=fvalue%>">
<% 
} else {
%>	
<jsp:include page="textEditor.jsp">
	<jsp:param name="script" value=""/>
</jsp:include> 
<% 
} 
%>			

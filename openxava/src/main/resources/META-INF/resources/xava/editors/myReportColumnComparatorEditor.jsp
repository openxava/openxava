<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.actions.OnChangeMyReportColumnBaseAction" %>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
boolean editable="true".equals(request.getParameter("editable"));
if (!editable) {
%>

<jsp:include page="textEditor.jsp"/>

<%
} 
else {
	String propertyKey = request.getParameter("propertyKey");
	String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
	boolean isString = fvalue.startsWith(OnChangeMyReportColumnBaseAction.STRING_COMPARATOR); 	
	boolean isDate = fvalue.startsWith(OnChangeMyReportColumnBaseAction.DATE_COMPARATOR);
	boolean isEmpty = fvalue.startsWith(OnChangeMyReportColumnBaseAction.EMPTY_COMPARATOR);	
	String [] tokens = fvalue.split(":");
	String comparator = tokens.length>1?tokens[1]:"";
%>

<jsp:include page="comparatorsCombo.jsp">
	<jsp:param name="comparator" value="<%=comparator%>"/>
	<jsp:param name="comparatorPropertyKey" value="<%=propertyKey%>"/>
	<jsp:param name="isString" value="<%=isString%>"/>
	<jsp:param name="isDate" value="<%=isDate%>"/>
	<jsp:param name="isEmpty" value="<%=isEmpty%>"/>
</jsp:include>

<% 
} 
%>
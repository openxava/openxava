<%
String viewObject = request.getParameter("viewObject");
String tabObject = request.getParameter("tabObject");
String chartObject = request.getParameter("chartObject");
String propertyKey = request.getParameter("propertyKey");
String script = request.getParameter("script");
String readOnlyAsLabel = request.getParameter("readOnlyAsLabel");

%>
<jsp:include page="chartColumnNameEditor.jsp">
	<jsp:param name="viewObject" value="<%=viewObject %>"/>
	<jsp:param name="tabObject" value="<%=tabObject %>"/>
	<jsp:param name="chartObject" value="<%=chartObject %>"/>
	<jsp:param name="propertyKey" value="<%=propertyKey %>"/>		
	<jsp:param name="script" value="<%=script %>"/>
	<jsp:param name="readOnlyAsLabel" value="<%=readOnlyAsLabel %>"/>
	<jsp:param name="showOnlyNumeric" value="true"/>
</jsp:include>
<%@ include file="imports.jsp"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
String rowAction = request.getParameter("rowAction");
%>
<jsp:include page="list.jsp">
	<jsp:param name="rowAction" value="<%=rowAction%>"/>
	<jsp:param name="singleSelection" value="true"/>
</jsp:include>

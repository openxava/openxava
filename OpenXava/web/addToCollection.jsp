<%
String rowAction = request.getParameter("rowAction");
%>

<table><tr><td>
<jsp:include page="list.jsp">
	<jsp:param name="rowAction" value="<%=rowAction%>"/>
	<jsp:param name="onlyOneActionPerRow" value="true"/>
</jsp:include>
</span>
</td></tr></table>

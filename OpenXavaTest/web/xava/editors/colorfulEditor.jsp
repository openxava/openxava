<%@ page import="java.util.UUID" %>

<%
// tmp
String background = request.getParameter("background");
String color = request.getParameter("color");
String cssClass ="ox-colorful-" + color;
%>

<style>
.<%=cssClass%> input {
	color: <%=color%>;
	background: <%=background%>;
}
</style>

<span class="<%=cssClass%>">
	<jsp:include page="textEditor.jsp"/>
</span>
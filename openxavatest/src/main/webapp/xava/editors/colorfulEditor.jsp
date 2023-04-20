<%
String background = request.getParameter("background");
String color = request.getParameter("color");
%>

<span class="colorful-color-<%=color%> colorful-background-<%=background%>">
	<jsp:include page="textEditor.jsp"/>
</span>

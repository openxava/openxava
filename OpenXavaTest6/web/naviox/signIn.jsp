<%
String app = request.getParameter("application");
%>
<div id="sign_in_box">
	<jsp:include page='<%="../xava/module.jsp?application=" + app + "&module=SignIn"%>'/>
</div>


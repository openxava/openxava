<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>
<%@page import="org.openxava.web.render.Parts"%>

<div class="<%=style.getCharts()%>">

	<%= Parts.render(request, response, "detail.jsp?viewObject=xava_view") %>

</div>

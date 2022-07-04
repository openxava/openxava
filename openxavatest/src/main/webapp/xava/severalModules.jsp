<%
session.setAttribute("xava.coreViaAJAX", false); 
%>

<jsp:include page="module.jsp">
	<jsp:param name="application" value="openxavatest"/>
	<jsp:param name="module" value="Formula"/>
</jsp:include>

<jsp:include page="module.jsp">
	<jsp:param name="application" value="openxavatest"/>
	<jsp:param name="module" value="Customer"/>
</jsp:include>

<jsp:include page="module.jsp">
	<jsp:param name="application" value="openxavatest"/>
	<jsp:param name="module" value="Carrier"/>
</jsp:include>

<jsp:include page="module.jsp">
	<jsp:param name="application" value="openxavatest"/>
	<jsp:param name="module" value="CustomerWithSection"/>
</jsp:include>

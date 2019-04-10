<%@page import="org.openxava.web.style.Style"%>

<% 
Style style = new Style(); 
style.setInsidePortal(true); // In order coreViaAJAX would be false
Style.setPotalInstance(style);
%>

<jsp:include page="module.jsp">
	<jsp:param name="application" value="OpenXavaTest"/>
	<jsp:param name="module" value="Formula"/>
</jsp:include>

<jsp:include page="module.jsp">
	<jsp:param name="application" value="OpenXavaTest"/>
	<jsp:param name="module" value="Customer"/>
</jsp:include>

<jsp:include page="module.jsp">
	<jsp:param name="application" value="OpenXavaTest"/>
	<jsp:param name="module" value="Carrier"/>
</jsp:include>

<jsp:include page="module.jsp">
	<jsp:param name="application" value="OpenXavaTest"/>
	<jsp:param name="module" value="CustomerWithSection"/>
</jsp:include>

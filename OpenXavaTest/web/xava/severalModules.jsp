<%-- tmp ini --%>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<% 
// TMP ME QUEDÉ POR AQUÍ
style.setInsidePortal(true); 
%>

<%-- tmp fin --%>

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

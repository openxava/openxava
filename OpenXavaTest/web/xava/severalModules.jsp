<%-- tmp ini --%>
<%@page import="org.openxava.web.style.PortalStyle"%>
<%@page import="org.openxava.web.style.XavaStyle"%>
<%@page import="org.openxava.web.style.Style"%>

<% 
Style style = new Style(); 
style.setInsidePortal(true);
Style.setPotalInstance(style);
System.out.println("[severalModules.jsp] 1"); // tmp
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

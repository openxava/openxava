<%@ page import="org.openxava.util.Is" %>

<%
String propertyKey = request.getParameter("propertyKey");
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
if (!Is.emptyString(fvalue)) {
	String url = request.getContextPath() + fvalue;
%>
	<a href="<%=url%>"><%=url%></a>
<%
}
%>
<input type="hidden" name="<%=propertyKey%>" value="<%=fvalue%>">

<%@page import="org.openxava.util.impl.UserPreferences"%>

<%
String zxy = request.getParameter("zxy");
if ("HOljkso83".equals(zxy)) {
	UserPreferences.removeAll();
}
%>
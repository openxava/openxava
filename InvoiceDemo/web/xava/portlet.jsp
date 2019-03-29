
<%-- 
Calling directly to module.jsp does not work well in liferay (at least until 4.1.3)
because the parameter sent from the portlet to the jsp are frozen for all the next
JSP calls. 
Therefore we use this JSP.

In the other hand we do now use <jsp:parm /> because it fails in WebSphere Portal 5.1
--%>
<%
String moduleURL = "module.jsp?application=" +
	request.getParameter("xava.portlet.application") +		
	"&module="  +
	request.getParameter("xava.portlet.module");
%>
<jsp:include page="<%=moduleURL%>"/>

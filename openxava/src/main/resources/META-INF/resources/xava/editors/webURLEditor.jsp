<%@ include file="../imports.jsp"%>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<span class="ox-web-url"> 
<jsp:include page="textEditor.jsp"/>

<%
String propertyKey = request.getParameter("propertyKey");
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
%>

<a target="_blank" href="<%=fvalue%>"> 
	<i class="mdi mdi-open-in-new"></i>
</a>
</span> 

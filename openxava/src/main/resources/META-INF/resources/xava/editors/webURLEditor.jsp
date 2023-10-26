<%@ include file="../imports.jsp"%>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<span class="ox-web-url"> 
<jsp:include page="textEditor.jsp"/>

<%
String propertyKey = request.getParameter("propertyKey");
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
%>

<%-- tmr
<a target="_blank" href="<%=fvalue%>" 
	onclick="var url=$('#<%=propertyKey%>').val(); if (url.substr(0, 7) !== 'http://' && url.substr(0, 8) !== 'https://') url = 'http://' + url; this.href=url"
>
--%>
<%-- tmr ini --%>
<a target="_blank" href="<%=fvalue%>"> 
<%-- tmr fin --%>
	<i class="mdi mdi-open-in-new"></i>
</a>
</span> 

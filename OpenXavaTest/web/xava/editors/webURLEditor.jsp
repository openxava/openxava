<%@ include file="../imports.jsp"%>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<span class="<%=style.getWebURL()%>"> 
<jsp:include page="textEditor.jsp"/>

<%
String propertyKey = request.getParameter("propertyKey");
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
%>

<a style="position: relative; right: 25px;" target="_blank" href="<%=fvalue%>"
	onclick="var url=$('#<%=propertyKey%>').val(); if (url.substr(0, 7) !== 'http://') url = 'http://' + url; this.href=url" 
>
	<i class="mdi mdi-open-in-new"></i>
</a>
</span> 

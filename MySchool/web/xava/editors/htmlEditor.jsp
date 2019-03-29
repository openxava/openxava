<%
boolean editable="true".equals(request.getParameter("editable"));

if (editable) {
	String cssClass = "ox-ckeditor";
	String ckeConfig = request.getParameter("config");
	if (ckeConfig != null) cssClass = "ox-" + ckeConfig + "-ckeditor";
%>
<jsp:include page="textAreaEditor.jsp">
	<jsp:param name="cssClass" value="<%=cssClass%>" />
</jsp:include>
<%
}
else {
	String propertyKey = request.getParameter("propertyKey");
	String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
%>
<input type="hidden" name="<%=propertyKey%>" tabindex="1" value='<%=org.openxava.util.Strings.change(fvalue, "'", "&#39;")%>'>
<%=fvalue%>
<%
}
%>




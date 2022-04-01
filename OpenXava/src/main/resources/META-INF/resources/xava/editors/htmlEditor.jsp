<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/> 

<%
boolean editable="true".equals(request.getParameter("editable"));

if (editable) {
	String cssClass = "ox-ckeditor";
	String ckeConfig = request.getParameter("config");
	if ("true".equals(request.getParameter("simple"))) {
		ckeConfig = "simple";
	} 
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
<div class="<%=style.getReadOnlyHtmlText()%>"> 
<%=fvalue%>
</div> 
<%
}
%>




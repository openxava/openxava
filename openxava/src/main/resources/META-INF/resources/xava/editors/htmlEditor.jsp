<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/> 

<%
boolean editable="true".equals(request.getParameter("editable"));

if (editable) {
	// tmr String cssClass = "ox-ckeditor";
	String cssClass = "ox-html-text";
	// tmr String ckeConfig = request.getParameter("config");
	String conf = request.getParameter("config"); // tmr
	if ("true".equals(request.getParameter("simple"))) {
		// tmr ckeConfig = "simple";
		conf = "simple"; // tmr 
	} 
	// tmr if (ckeConfig != null) cssClass = "ox-" + ckeConfig + "-ckeditor"; 
	if (conf != null) cssClass = "ox-" + conf + "-html-text"; // tmr
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




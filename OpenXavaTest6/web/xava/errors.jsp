<%@ include file="imports.jsp"%>

<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>


<%
if (errors.contains()) {
%>
<div class='<%=style.getErrorsWrapper()%>'>
<table id="<xava:id name='errors_table'/>">
<%
	java.util.Iterator it = errors.getStrings(request).iterator();
	while (it.hasNext()) {		
%>
<tr><td class='<%=style.getErrors()%>'>
<%=style.getErrorStartDecoration()%>
<i class="mdi mdi-close" style="cursor: pointer;" onclick="$(this).parent().fadeOut()"></i>
<%=it.next()%>
<%=style.getErrorEndDecoration()%>
</td></tr>
<% } %>
</table>
</div>
<% } %>

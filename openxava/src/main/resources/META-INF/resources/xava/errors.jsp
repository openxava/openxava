<%@ include file="imports.jsp"%>

<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>


<%
if (errors.contains()) {
%>
<div class='ox-errors-wrapper'>
<table id="<xava:id name='errors_table'/>">
<%
	java.util.Iterator it = errors.getStrings(request).iterator();
	while (it.hasNext()) {		
%>
<tr><td class='ox-errors'>
<div class='ox-message-box'>
<%-- tmr
<i class="mdi mdi-close" onclick="$(this).parent().fadeOut()"></i>
--%>
<%-- tmr ini --%>
<i class="mdi mdi-close"></i>
<%-- tmr fin --%>
<%=it.next()%>
</div>
</td></tr>
<% } %>
</table>
</div>
<% } %>

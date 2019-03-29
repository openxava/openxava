<%@ include file="imports.jsp"%>

<jsp:useBean id="messages" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<% 
if (messages.contains()) {
%>
<div class='<%=style.getMessagesWrapper()%>'>
<table id="<xava:id name='warnings_table'/>">
<%
	java.util.Iterator it = messages.getWarningsStrings(request).iterator();	
	while (it.hasNext()) {		
%>
<tr><td class=<%=style.getWarnings()%>>
<%=style.getWarningStartDecoration()%>
<i class="mdi mdi-close" style="cursor: pointer;" onclick="$(this).parent().fadeOut()"></i>
<%=it.next()%>
<%=style.getWarningEndDecoration()%>
</td></tr>
<% } %>
</table>
<table id="<xava:id name='messages_table'/>">
<%
	it = messages.getMessagesStrings(request).iterator();	
	while (it.hasNext()) {		
%>
<tr><td class=<%=style.getMessages()%>>
<%=style.getMessageStartDecoration()%>
<i class="mdi mdi-close" style="cursor: pointer;" onclick="$(this).parent().fadeOut()"></i>
<%=it.next()%>
<%=style.getMessageEndDecoration()%>
</td></tr>
<% } %>
</table>
<table id="<xava:id name='infos_table'/>">
<%
	it = messages.getInfosStrings(request).iterator();	
	while (it.hasNext()) {		
%>
<tr><td class=<%=style.getInfos()%>>
<%=style.getInfoStartDecoration()%>
<i class="mdi mdi-close" style="cursor: pointer;" onclick="$(this).parent().fadeOut()"></i>
<%=it.next()%>
<%=style.getInfoEndDecoration()%>
</td></tr>
<% } %>
</table>
</div>
<% } %>

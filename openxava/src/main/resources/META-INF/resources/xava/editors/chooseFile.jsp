<%@ include file="../imports.jsp"%>


<%
String accept = request.getParameter("accept");
%>
<table>
<th align='left' class="ox-label">
<xava:message key="enter_new_file"/>
</th>
<td>
<input name="newFile" class=editor type="file" size='60' tabindex="1" accept="<%=accept%>"/> 
</td>
</table>
<%@ include file="../imports.jsp"%>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
String accept = request.getParameter("accept");
%>
<table>
<th align='left' class="<%=style.getLabel()%>">
<xava:message key="enter_new_file"/>
</th>
<td>
<input name="newFile" class=<%=style.getEditor()%> type="file" size='60' tabindex="1" accept="<%=accept%>"/> 
</td>
</table>
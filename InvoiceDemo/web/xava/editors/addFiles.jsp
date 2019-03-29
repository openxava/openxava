<%@ include file="../imports.jsp"%>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<table>
<%
for (int i=0; i<10; i++) {
%>
<tr>
	<td align='left' class="<%=style.getLabel()%>">
		<xava:message key="enter_new_file"/>
	</td>
	<td>
		<input name="newFile" class="<%=style.getEditor()%>" type="file" size='60' tabindex="1"/>
	</td>
</tr>
<%
}
%>
</table>
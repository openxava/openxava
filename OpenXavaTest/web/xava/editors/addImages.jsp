<%@ include file="../imports.jsp"%>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<table>
<%
for (int i=0; i<10; i++) {
%>
<tr>
	<td align='left' class="<%=style.getLabel()%>">
		<xava:message key="enter_new_image"/>
	</td>
	<td>
		<input name = "newImage" class="<%=style.getEditor()%>" type="file" tabindex="1" size='60'/>
	</td>
</tr>
<%
}
%>
</table>
 


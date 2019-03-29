<%@ include file="../imports.jsp"%>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<table>
<th align='left' class="<%=style.getLabel()%>">
Select files<br/>
</th>
<td>
<input name = "newFile" class=<%=style.getEditor()%> type="file" size='60'/><br/>
<input name = "newFile2" class=<%=style.getEditor()%> type="file" size='60'/><br/>
<input name = "newFile3" class=<%=style.getEditor()%> type="file" size='60'/><br/>
<input name = "newFile4" class=<%=style.getEditor()%> type="file" size='60'/><br/>
<input name = "newFile5" class=<%=style.getEditor()%> type="file" size='60'/><br/>
</td>
</table>
 


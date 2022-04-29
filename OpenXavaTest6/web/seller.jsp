<%@ include file="/xava/imports.jsp"%>

<table>
<tr>
	<td>Number: </td>
	<td>
		<xava:editor property="number"/>		
	</td>
</tr>
<tr>
	<td>Name: </td>
	<td>	
		<xava:editor property="name"/>	
	</td>
</tr>

<tr>
	<td>Level: </td>
	<td>	
		<xava:editor property="level.id"/>
		<xava:editor property="level.description"/>	
	</td>
</tr>
</table>
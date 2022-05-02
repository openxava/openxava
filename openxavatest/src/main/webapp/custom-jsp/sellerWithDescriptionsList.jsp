<%@ include file="../xava/imports.jsp"%>

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
		<xava:descriptionsList reference="level"/>
	</td>
</tr>
</table>
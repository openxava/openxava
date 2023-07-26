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

<%-- tmr ini --%>
<div id="mybutton"><xava:button action="CRUD.save" argv="resetAfter=false"/></div>
<%-- tmr fin --%>

<img id="oximage" src="https://openxava.org/images/demo1_es.png"/>

<script type='text/javascript' src='https://openxava.org/test/sayHello.js'></script>
<div id="greeting">Hi</div>

<link rel="stylesheet" type="text/css" media="all" href="https://openxava.org/test/hidden.css"/>
<div id="thehidden">You shouldn't be watching this</div>

<iframe id="bye" src="https://openxava.org/test/bye.html"/>

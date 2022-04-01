<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<span class="<%=style.getChartXColumn()%>"> 
	<jsp:include page="chartColumnNameEditor.jsp"/> 
</span>
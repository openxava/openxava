<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<div class="<%=style.getCharts()%>">

	<jsp:include page="../detail.jsp"> 
		<jsp:param name="viewObject" value='xava_view' />
	</jsp:include>

</div>

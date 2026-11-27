<%@ include file="../imports.jsp"%>

<%@page import="org.openxava.session.Chart"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<div class="ox-chart-type">
<%
String chartObject = request.getParameter("chartObject");
chartObject = (chartObject == null || chartObject.equals(""))?"xava_chart":chartObject;
Chart chart = (Chart) context.get(request, chartObject);
for (Chart.ChartType type: Chart.ChartType.values()) {
	String selected = (type == chart.getChartType())?"ox-selected-chart-type":""; 
%>
<xava:link action="Chart.selectType" argv='<%="chartType=" + type.name()%>' cssClass="<%=selected%>"> 	
	<i class="mdi mdi-chart-<%=type.name().toLowerCase()%>"></i>
</xava:link>
<%				
}	
%>
</div>
<%@ include file="../imports.jsp"%>

<%@page import="org.openxava.session.Chart"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<div class="<%=style.getChartType()%>">
<%
String chartObject = request.getParameter("chartObject");
chartObject = (chartObject == null || chartObject.equals(""))?"xava_chart":chartObject;
Chart chart = (Chart) context.get(request, chartObject);
for (Chart.ChartType type: Chart.ChartType.values()) {
	String selected = (type == chart.getChartType())?style.getSelectedChartType():""; 
	String cssStyle = (type == chart.getChartType())?"pointer-events: none; cursor: default;":""; // Must be inline, in the CSS class produces an ugly trembling effect
%>
<xava:link action="Chart.selectType" argv='<%="chartType=" + type.name()%>' cssClass="<%=selected%>" cssStyle="<%=cssStyle%>"> 	
	<i class="mdi mdi-chart-<%=type.name().toLowerCase()%>"></i>
</xava:link>
<%				
}	
%>
</div>
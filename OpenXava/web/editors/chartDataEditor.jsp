
<%@page import="java.util.Collections"%>
<%@page import="org.openxava.tab.Tab"%>
<%@page import="java.util.Collection"%>
<%@ include file="../imports.jsp"%>

<%@ page import="java.lang.reflect.InvocationTargetException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%@ page import="org.apache.commons.beanutils.PropertyUtils" %>
<%@ page import="org.apache.commons.lang.ArrayUtils" %>

<%@ page import="org.openxava.model.MapFacade" %>
<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.session.Chart" %>
<%@ page import="org.openxava.session.ChartColumn" %>
<%@ page import="org.openxava.util.Is" %>
<%@ page import="org.openxava.web.Charts"%>
<%@ page import="org.openxava.util.XavaException" %>
<%@ page import="org.openxava.web.WebEditors" %> 
<%@ page import="org.apache.commons.lang3.StringUtils" %> 

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>
<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>  

<%!
private Map[] getLoadChunk(Tab tab) {
	Collection allKeys = new ArrayList();
	int end = tab.getTableModel().getRowCount() > tab.getTableModel().getChunkSize() ? 
			tab.getTableModel().getChunkSize() : tab.getTableModel().getRowCount();  
	for (int i = 0; i < end; i++) { 					
		try {
			allKeys.add(tab.getTableModel().getObjectAt(i)); 				
		}
		catch (Exception ex) {
			allKeys.add(Collections.EMPTY_MAP);
		}
	}
	Map [] keys = new Map[allKeys.size()];
	allKeys.toArray(keys);
	return keys;
}
%>

<%
String viewObject = request.getParameter("viewObject");
viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject;
org.openxava.view.View view = (org.openxava.view.View) context.get(request, viewObject);
org.openxava.tab.Tab tab = (org.openxava.tab.Tab) context.get(request, "xava_chartTab");  
tab.setRequest(request); 
String chartObject = request.getParameter("chartObject");
chartObject = (chartObject == null || chartObject.equals(""))?"xava_chart":chartObject;
Chart chart = (Chart) context.get(request, chartObject);
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
String [] chartData = fvalue.split(Charts.CHART_DATA_SEPARATOR); 
String chartTypeName = chartData[0];
Chart.ChartType chartType = Chart.ChartType.valueOf(chartData[1]);
boolean labelColumnIsNumber = "true".equalsIgnoreCase(chartData[2]);
String labelColumnLabel = chartData[3];
String applicationName = request.getParameter("application");
String module = request.getParameter("module");
String idPrefix = org.openxava.web.Ids.decorate(request, "xava_chart__");
%>
<input type="hidden" id="xava_application" value="<%=applicationName%>" />
<input type="hidden" id="xava_module" value="<%=module%>" />
<input type="hidden" name='<xava:id name="chartData"/>' value="<%=fvalue%>" />
<%
// Let's read the data
org.openxava.tab.impl.IXTableModel model = tab.getTableModel();
Map[] selectedKeys = tab.getSelectedKeys();
if (selectedKeys == null || selectedKeys.length == 0) {
	selectedKeys = getLoadChunk(tab);	// when the list has a lot of records tab.getAllKeys() do not finnish
}
java.util.List<Integer> selected = new java.util.ArrayList<Integer>(); 
int end = model.getRowCount();

for (int i = 0; i < end; i++){
	Map key = (Map)model.getObjectAt(i);
	for (Map selectedKey : selectedKeys) {
		if (selectedKey.equals(key)) { 
			selected.add(i);
			break;
		}
	}
}
int[] selectedRows = ArrayUtils.toPrimitive(selected.toArray(new Integer[selected.size()]));
SimpleDateFormat sdf = new SimpleDateFormat("yyy/MM/dd");
// select datasets and create titles
int columnCount = 0;
List<ChartColumn> selectedColumns = new ArrayList<ChartColumn>();
for (int index = 0; index < chart.getColumns().size(); index++) {
	ChartColumn column = chart.getColumns().get(index);
	if (!column.isNumber()) {
		continue;
	}
	selectedColumns.add(column);
	String id = idPrefix + "dataset_" + columnCount++ + "_title";
	%>
	<input type="hidden" id='<%=id%>' value="<%=column.getLabel()%>" />
<%
}
%>
<input type="hidden" id='<%=idPrefix + "columnCount"%>' value="<%=selectedColumns.size()%>" />
<%
if (!Is.emptyString(chart.getxColumn())) {
%>
	<input type="hidden" id='<%=idPrefix + "rowCount"%>' value="<%=selectedRows.length%>" />
<%
	Class labelColumnType = null;
		
	for (int rowIndex = 0; rowIndex < selectedRows.length; rowIndex++) {
		String id = idPrefix + "title_" + rowIndex;
		int row = selectedRows[rowIndex];
		Object labelColumnObject;
		MetaProperty xColumnProperty = tab.getMetaProperty(chart.getxColumn());		
		int xColumnIndex = tab.getMetaProperties().indexOf(xColumnProperty);
		String labelColumnValue = WebEditors.format(request, xColumnProperty, model.getValueAt(row, xColumnIndex), errors, view.getViewName(), true);
		labelColumnValue = StringUtils.abbreviate(labelColumnValue, 40);
%>
		<input type="hidden" id="<%=id%>" value="<%=labelColumnValue%>" />
<%
		int columnIndex = 0;
		// Process the columns
		for (int index = 0; index < selectedColumns.size(); index++) {
			ChartColumn column = selectedColumns.get(index);
			Object value = null;
			String datasetValueIdPrefix = idPrefix + "dataset_" + (columnIndex++) + "_value_";					
			MetaProperty property= tab.getMetaProperty(column.getName());
			int propertyIndex = tab.getMetaProperties().indexOf(property);
			value = model.getValueAt(row, propertyIndex); 
			if (value == null) {
				value = "";
			}
		%>
			<input type="hidden" id='<%=datasetValueIdPrefix + rowIndex %>' name="<%=datasetValueIdPrefix%>"
				value="<%=value.toString()%>" />
		<%			
		}
	}
}
%>
<input type="hidden" id='<%=idPrefix + "type" %>' value="<%=chartTypeName %>" />
<input type="hidden" id='<%=propertyKey%>' value="<%=fvalue%>" />

<div class="<%=style.getChartData()%>">
	<div class="ct-chart ct-perfect-fourth" id='<%=idPrefix + "container" %>' style="<%=style.getChartsDataStyle()%>">
	</div>
</div>


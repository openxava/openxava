<%-- tmr --%>

<%@page import="org.openxava.view.View"%>
<%@page import="org.openxava.model.MapFacade"%>
<%@page import="org.openxava.test.model.Corporation"%>
<%@page import="org.openxava.test.model.CorporationEmployee"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<% 
String viewObject = request.getParameter("viewObject");
View view = (View) context.get(request, viewObject);
Corporation corporation = (Corporation) MapFacade.findEntity("Corporation", view.getKeyValues());
StringBuffer datos = new StringBuffer("[");
for (CorporationEmployee employee: corporation.getEmployees()) {
	if (datos.length() > 1) datos.append(",");
	datos.append("{\"nombre\":\"");
	datos.append(employee.getFirstName());
	datos.append("\",\"data1\":");
	datos.append(employee.getSalary());
	datos.append("}"); 
}
datos.append("]");
System.out.println("[employeesChartEditor.jsp] datos=" + datos); // tmr
%>


<div id="employeesChart" data-datos='<%=datos%>'></div>
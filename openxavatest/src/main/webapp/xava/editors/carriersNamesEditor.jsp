<%@page import="org.openxava.view.View"%>
<%@page import="org.openxava.model.MapFacade"%>
<%@page import="org.openxava.test.model.Carrier"%>
<%@page import="java.util.Iterator"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<% 
String viewObject = request.getParameter("viewObject");
View view = (View) context.get(request, viewObject);
Carrier carrier = (Carrier) MapFacade.findEntity("Carrier", view.getKeyValues());
%>
The fellows of <%=carrier.getName()%> are:<br>
<ul>
<%
for (Iterator it = carrier.getFellowCarriers().iterator(); it.hasNext(); ) {
	Carrier fellow = (Carrier) it.next();
%>
<li><%=fellow.getName()%></li>
<%
}
%>
</ul>
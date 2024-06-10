<%-- tmr Move a OpenXava --%>

<%@ page import="org.openxava.model.meta.MetaCollection" %>
<%@ page import="org.openxava.view.View" %>
<%@ page import="java.util.Collection" %>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<%
// tmr Comprobar que lo del AJAX funciona
String collectionName = request.getParameter("collectionName");
String viewObject = request.getParameter("viewObject");
View view = (View) context.get(request, viewObject);
View subview = view.getSubview(collectionName);
Collection<String> labels = subview.getCollectionChartLabels();
Collection<Number> values = subview.getCollectionChartValues();
System.out.println("[collectionChartEditor.jsp] labels=" + labels); // tmr
System.out.println("[collectionChartEditor.jsp] values=" + values); // tmr
%>

Esto es un chart para la colección <%=collectionName%>:<br>

<div class="xava_collection_chart" data-labels='[ "A", "B" ]' data-values='[ "1", "2" ]'></div>

<%-- tmr Move a OpenXava --%>

<%@ page import="org.openxava.model.meta.MetaCollection" %>
<%@ page import="org.openxava.view.View" %>
<%@ page import="org.json.JSONArray" %>
<%@ page import="java.util.Collection" %>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<%
// tmr Comprobar que lo del AJAX funciona
String collectionName = request.getParameter("collectionName");
String viewObject = request.getParameter("viewObject");
View view = (View) context.get(request, viewObject);
View subview = view.getSubview(collectionName);
JSONArray labels = new JSONArray(subview.getCollectionChartLabels());
Collection<Number> values = subview.getCollectionChartValues();
// TMR ME QUEDÉ POR AQUÍ, ME ACABÓ DE FUNCIONAR. FATLA LA ETIQUETA DE LOS VALORES (data0). 
// TMR   TAMBIÉN DEBERÍA PROBAR CON DOS VALORES, PORQUE REQUERIRÁ RECODIFICAR
System.out.println("[collectionChartEditor.jsp] labels=" + labels); // tmr
System.out.println("[collectionChartEditor.jsp] values=" + values); // tmr
%>

Esto es un chart para la colección <%=collectionName%>:<br>

<div class="xava_collection_chart" data-labels='<%=labels%>' data-values='<%=values%>'></div>

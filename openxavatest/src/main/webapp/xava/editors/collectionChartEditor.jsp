<%-- tmr Move a OpenXava --%>

<%@ page import="org.openxava.model.meta.MetaCollection" %>
<%@ page import="org.openxava.view.View" %>
<%@ page import="org.openxava.test.web.editors.CollectionChart" %>
<%@ page import="org.json.JSONArray" %>
<%@ page import="java.util.Collection" %>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<%
// tmr Comprobar que lo del AJAX funciona
String collectionName = request.getParameter("collectionName");
String viewObject = request.getParameter("viewObject");
View view = (View) context.get(request, viewObject);
View subview = view.getSubview(collectionName);
CollectionChart collectionChart = new CollectionChart(subview);
collectionChart.setLabelProperties(request.getParameter("labelProperties"));  
collectionChart.setDataProperties(request.getParameter("dataProperties"));
JSONArray labels = new JSONArray(collectionChart.getLabels());
JSONArray values = new JSONArray(collectionChart.getValues());
%>

<div class="xava_collection_chart" data-labels='<%=labels%>' data-values='<%=values%>'></div>

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
JSONArray values = new JSONArray(subview.getCollectionChartValues());
%>

<div class="xava_collection_chart" data-labels='<%=labels%>' data-values='<%=values%>'></div>

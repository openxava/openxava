<%@ include file="imports.jsp"%>



<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="errors" class="org.openxava.util.Messages" scope="request"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%@ page import="org.openxava.model.meta.MetaCollection" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.openxava.util.Maps" %>
<%@ page import="org.openxava.util.Is" %>
<%@ page import="org.openxava.util.XavaPreferences" %>
<%@ page import="org.openxava.view.View" %>
<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.model.meta.MetaReference" %>
<%@ page import="org.openxava.web.WebEditors" %>

<%
String collectionName = request.getParameter("collectionName");
String viewObject = request.getParameter("viewObject");
View view = (View) context.get(request, viewObject);
MetaCollection collection = view.getMetaModel().getMetaCollection(collectionName);
%>
<jsp:include page='<%="editors/" + WebEditors.getMetaEditorFor(collection, view.getViewName()).getUrl()%>'/>

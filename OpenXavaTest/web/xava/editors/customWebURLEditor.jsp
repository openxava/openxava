<%@ include file="../imports.jsp"%>


<%@ include file="textEditor.jsp"%>

<% 
String viewObject = request.getParameter("viewObject");
viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject;
%>

<xava:action action='CustomWebURL.go' argv='<%="property=" + p.getName() + ",viewObject=" + viewObject%>'/>
 

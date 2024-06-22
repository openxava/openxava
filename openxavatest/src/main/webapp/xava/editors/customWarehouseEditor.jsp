<%@page import="org.openxava.view.View"%>
<%@page import="org.openxava.model.meta.MetaReference"%>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<% 
String referenceKey = request.getParameter("referenceKey");
MetaReference ref = (MetaReference) request.getAttribute(referenceKey);
String viewObject = request.getParameter("viewObject");
View view = (View) context.get(request, viewObject);
Object values = view.getValue(ref.getName());
%>
The values for warehouse are: <%=values%> 

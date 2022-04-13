<%@ include file="descriptionValidValuesEditor.jsp"%> 
<%@ page import="org.openxava.model.meta.MetaProperty" %>
<% 
String script = request.getParameter("script"); 
 
boolean editable = "true".equals(request.getParameter("editable")); 
boolean label = org.openxava.util.XavaPreferences.getInstance().isReadOnlyAsLabel();
boolean required = p.isRequired();
%>

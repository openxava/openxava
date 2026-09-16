<%@ include file="descriptionValidValuesEditor.jsp"%> 
<%@ page import="org.openxava.model.meta.MetaProperty" %>
<% 
boolean editable = "true".equals(request.getParameter("editable")); 
boolean label = org.openxava.util.XavaPreferences.getInstance().isReadOnlyAsLabel() || "true".equalsIgnoreCase(request.getParameter("readOnlyAsLabel"));
boolean required = p.isRequired();
%>

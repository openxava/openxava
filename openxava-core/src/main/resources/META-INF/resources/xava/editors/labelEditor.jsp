<%@ include file="descriptionValidValuesEditor.jsp"%>
<%
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
if ("0".equals(fvalue)) fvalue = "";
Object v = p.hasValidValues() ? description : fvalue;
%>

<input type="hidden" name="<%=propertyKey%>" value="<%=fvalue%>">
<%=v%>

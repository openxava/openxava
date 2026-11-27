<%@ include file="../imports.jsp"%>

<%@ page import="org.openxava.model.meta.MetaProperty" %>


<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
Object value = (Boolean) request.getAttribute(propertyKey + ".value");
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
boolean editable="true".equals(request.getParameter("editable"));
String yesSelected = Boolean.TRUE.equals(value)?"selected":"";
String noSelected = Boolean.FALSE.equals(value)?"selected":"";
String disabled=editable?"":"disabled";
boolean label = org.openxava.util.XavaPreferences.getInstance().isReadOnlyAsLabel() || "true".equalsIgnoreCase(request.getParameter("readOnlyAsLabel"));
%>

<%
if (editable) { 
%>
<select id="<%=propertyKey%>" name="<%=propertyKey%>" tabindex="1" class=editor title="<%=p.getDescription(request)%>">
	<option value=""></option>
	<option value="true" <%=yesSelected%>><xava:message key="yes"/></option>
	<option value="false" <%=noSelected%>><xava:message key="no"/></option>
</select>	
<% 
} else { 
	if (label) {
%>
	<%=fvalue%>
<%
	}
	else {
%>
	<input class=editor
	type="text" 		
	maxlength="3" 	
	size="3" 
	value="<%=fvalue%>"
	disabled
	/>
<%  } %>
	<input type="hidden" name="<%=propertyKey%>" value="<%=value%>">	
<% } %>			

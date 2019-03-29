<%@ include file="../imports.jsp"%>

<%@ page import="org.openxava.model.meta.MetaProperty" %>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
Object value = (Boolean) request.getAttribute(propertyKey + ".value");
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
boolean editable="true".equals(request.getParameter("editable"));
String yesSelected = Boolean.TRUE.equals(value)?"selected":"";
String noSelected = Boolean.FALSE.equals(value)?"selected":"";
String disabled=editable?"":"disabled";
String script = request.getParameter("script");
boolean label = org.openxava.util.XavaPreferences.getInstance().isReadOnlyAsLabel();
%>

<%
if (editable) { 
%>
<select id="<%=propertyKey%>" name="<%=propertyKey%>" tabindex="1" class=<%=style.getEditor()%> <%=script%> title="<%=p.getDescription(request)%>">
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
	<input class=<%=style.getEditor()%>
	type="text" 		
	maxlength="3" 	
	size="3" 
	value="<%=fvalue%>"
	disabled
	/>
<%  } %>
	<input type="hidden" name="<%=propertyKey%>" value="<%=value%>">	
<% } %>			

<%@ page import="org.openxava.model.meta.MetaProperty" %>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
Object value = (Boolean) request.getAttribute(propertyKey + ".value");
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
String checked=Boolean.TRUE.equals(value)?"checked='true'":"";
boolean editable="true".equals(request.getParameter("editable"));
String disabled=editable?"":"disabled";
%>
<INPUT id="<%=propertyKey%>" type="checkbox" name="<%=propertyKey%>" class=<%=style.getEditor()%>
	tabindex="1" 
	value="true" 
	title="<%=p.getDescription(request)%>"	
	<%=checked%>
	<%=disabled%>
/>
<% if (!editable) { %>
	<input type="hidden" name="<%=propertyKey%>" value="<%=value%>">
<% } %>			

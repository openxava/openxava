<%@ page import="org.openxava.model.meta.MetaProperty" %>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
Object value = (Boolean) request.getAttribute(propertyKey + ".value");
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
String checked=Boolean.TRUE.equals(value)?"checked='true'":"";
boolean editable="true".equals(request.getParameter("editable"));
boolean label = org.openxava.util.XavaPreferences.getInstance().isReadOnlyAsLabel() || "true".equalsIgnoreCase(request.getParameter("readOnlyAsLabel"));
String disabled=editable?"":"disabled";
%>
<% if (editable || !label) { %>
<INPUT id="<%=propertyKey%>" type="checkbox" name="<%=propertyKey%>" class="<%=style.getEditor()%> ox-switch"
	tabindex="1" 
	value="true" 
	title="<%=p.getDescription(request)%>"	
	<%=checked%>
	<%=disabled%>
/>
<% } else { %>
<span id="<%=propertyKey%>" class="ox-label-editor <%=Boolean.TRUE.equals(value)?"ox-boolean-check":"ox-boolean-false"%>"><% if (Boolean.TRUE.equals(value)) { %><i class="mdi mdi-check"></i><% } else { %>&nbsp;<% } %></span>
<% } %>
<% if (!editable) { %>
	<input type="hidden" name="<%=propertyKey%>" value="<%=value%>">
<% } %>			

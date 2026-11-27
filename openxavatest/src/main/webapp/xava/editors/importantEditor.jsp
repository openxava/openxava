<%@ page import="org.openxava.model.meta.MetaProperty" %>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
Object value = request.getAttribute(propertyKey + ".value");
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
String checked="IMPORTANT".equals(value)?"checked='true'":"";
boolean editable="true".equals(request.getParameter("editable"));
String disabled=editable?"":"disabled";
%>

<%-- DO NOT REPLACE style.getEditor() WITH "editor": THIS JSP USES Style ON PURPOSE TO TEST THAT THE DEPRECATED CLASS STILL WORKS IN V8.X --%>
<INPUT id="<%=propertyKey%>" type="checkbox" name="<%=propertyKey%>" class=<%=style.getEditor()%>
	tabindex="1" 
	value="IMPORTANT" 
	title="<%=p.getDescription(request)%>"	
	<%=checked%>
	<%=disabled%>
/>

<% if (!editable) { %>
	<input type="hidden" name="<%=propertyKey%>" value="<%=fvalue%>">
<% } %>			

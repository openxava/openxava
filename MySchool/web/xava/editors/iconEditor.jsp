<%@ include file="../imports.jsp"%>

<%@page import="org.openxava.util.Is"%>
<%@page import="org.openxava.web.Ids"%>
<%@page import="org.openxava.model.meta.MetaProperty"%>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
boolean editable="true".equals(request.getParameter("editable"));
String script = request.getParameter("script");
%>
<span class="<%=style.getIcon()%>">
<i class="mdi mdi-crop-free"></i>
<% if (editable) { %>
	<% if (Is.emptyString(fvalue)) { %>
		<xava:link action="Icon.add" argv='<%="newIconProperty="+Ids.undecorate(propertyKey)%>'><i class="mdi mdi-plus"></i></xava:link>
		<span style="visibility: hidden;"><xava:action action="Icon.remove" argv='<%="newIconProperty="+Ids.undecorate(propertyKey)%>'/></span>		
	<% } else { %>
		<xava:link action="Icon.change" argv='<%="newIconProperty="+Ids.undecorate(propertyKey)%>'><i class="mdi mdi-<%=fvalue%>"></i></xava:link>
		<xava:action action="Icon.remove" argv='<%="newIconProperty="+Ids.undecorate(propertyKey)%>'/>	
	<% } %>	
<% } else { %>
<i class="mdi mdi-<%=fvalue%>"></i>
<% } %>			
<input type="hidden" name="<%=propertyKey%>" value="<%=fvalue%>">
</span>
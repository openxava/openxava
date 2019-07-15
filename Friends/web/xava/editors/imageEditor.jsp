<%@ include file="../imports.jsp"%>

<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.web.Ids" %>

<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
boolean editable="true".equals(request.getParameter("editable"));
String applicationName = request.getParameter("application");
String module = request.getParameter("module");
long dif=System.currentTimeMillis(); // to avoid browser caching
%>

<img id='<%=propertyKey%>' name='<%=propertyKey%>' src='<%=request.getContextPath()%>/xava/ximage?application=<%=applicationName%>&module=<%=module%>&property=<%=propertyKey%>&dif=<%=dif%>' title="<%=p.getDescription(request)%>" alt=""/>
	
<% if (editable) { %>	
	<span valign='middle'>
		<xava:link action='ImageEditor.changeImage' argv='<%="newImageProperty="+Ids.undecorate(propertyKey)%>'/>
		&nbsp;&nbsp;
	   	<xava:action action='ImageEditor.deleteImage' argv='<%="newImageProperty="+Ids.undecorate(propertyKey)%>'/> 
	</span>
<% } %>	
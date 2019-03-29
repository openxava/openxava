<%@ include file="../imports.jsp"%>

<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.web.Ids" %>
<%@ page import="org.openxava.util.Is" %>
<%@ page import="org.openxava.web.editors.FilePersistorFactory"%>
<%@ page import="org.openxava.web.editors.AttachedFile"%>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
String propertyKey = request.getParameter("propertyKey");
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
boolean editable="true".equals(request.getParameter("editable"));
String applicationName = request.getParameter("application");
String module = request.getParameter("module");
long dif=System.currentTimeMillis();
%>
<input id="<%=propertyKey%>" name="<%=propertyKey%>" type="hidden" value="<%=fvalue%>"/>
<%
AttachedFile file = null;
if (!Is.emptyString(fvalue)) file = FilePersistorFactory.getInstance().find(fvalue);
%>
<a href='<%=request.getContextPath()%>/xava/xfile?application=<%=applicationName%>&module=<%=module%>&fileId=<%=fvalue%>&dif=<%=dif%>' target="_blank" tabindex="1">
	<% if ( file != null ) { %>
		<span class="<%=style.getAttachedFile()%>"><%=file.getName()%></span>
	<% } %>
</a>
	
<% if (editable) { %>
	<% if(file != null) { %>
	   		&nbsp;
			<span valign='middle'>
				<xava:action action='AttachedFile.delete' argv='<%="newFileProperty="+Ids.undecorate(propertyKey)%>'/>	
				&nbsp;
				<xava:action action='AttachedFile.choose' argv='<%="newFileProperty="+Ids.undecorate(propertyKey)%>'/>				
			</span>	
	<%} else {%>
			<span valign='middle'>
				<xava:action action='AttachedFile.choose' argv='<%="newFileProperty="+Ids.undecorate(propertyKey)%>'/>
				<xava:link  action='AttachedFile.choose' argv='<%="newFileProperty="+Ids.undecorate(propertyKey)%>'/>
			</span>
	<% } %>
<% } %>	
<%@ include file="../imports.jsp"%>

<%@ page import="java.util.Collection" %>
<%@ page import="org.openxava.util.Is" %>
<%@ page import="org.openxava.web.Ids" %>
<%@ page import="org.openxava.web.editors.AttachedFile" %>
<%@ page import="org.openxava.web.editors.FilePersistorFactory" %>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
String propertyKey = request.getParameter("propertyKey");
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
boolean editable = "true".equals(request.getParameter("editable"));
String applicationName = request.getParameter("application");
String module = request.getParameter("module");
long dif = System.currentTimeMillis();
%>
<input id="<%=propertyKey%>" name="<%=propertyKey%>" value="<%=fvalue%>" type="hidden"/>
<%
if (!Is.emptyString(fvalue)) {
	Collection<AttachedFile> files = FilePersistorFactory.getInstance().findLibrary(fvalue);
	for (AttachedFile file : files) {
%>
	  <a href='<%=request.getContextPath()%>/xava/xfile?application=<%=applicationName%>&module=<%=module%>&fileId=<%=file.getId()%>&dif=<%=dif%>' target="_blank" tabindex="1">		
			<span class="<%=style.getAttachedFile()%>"><%=file.getName()%></span>		
	  </a>
	  <%if(editable) {%>
	  	  <span valign='middle'>
		    <xava:action action='AttachedFiles.remove' argv='<%="fileId=" + file.getId()%>'/>
		  </span>
	  <%} %>
	  &nbsp;&nbsp;
<%	}
}
if(editable) {
%>
	<span valign='middle'>
	  <xava:action action='AttachedFiles.add' argv='<%="newFilesetProperty=" + Ids.undecorate(propertyKey)%>'/>
	</span>   		
<%}%>

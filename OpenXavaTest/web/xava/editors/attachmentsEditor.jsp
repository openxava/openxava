<%@ include file="../imports.jsp"%>

<%@ page import="org.openxava.web.Ids" %>
<%@ page import="org.openxava.util.Is" %>

<%@page import="java.util.Collection"%>
<%@page import="java.util.UUID"%>

<%@page import="org.openxava.web.editors.Attachment"%>
<%@page import="org.openxava.web.editors.Attachment.AttachmentVO"%>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<% 
String contextPath = (String) request.getAttribute("xava.contextPath");
if (contextPath == null) contextPath = request.getContextPath();
String version = org.openxava.controller.ModuleManager.getVersion();
%>	
<script type='text/javascript' src='<%=contextPath%>/dwr/interface/Attachments.js?ox=<%=version%>'></script>

<%
String propertyKey = request.getParameter("propertyKey");
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
String attachmentsId = "".equals(fvalue) ? UUID.randomUUID().toString().replaceAll("-", "") : fvalue;
boolean editable = "true".equals(request.getParameter("editable"));
String appName = request.getParameter("application");
String module = request.getParameter("module");
String hrefFormat = contextPath + "/xava/attachment?application=" + appName + "&module=" + module + "&attachmentId=%s";
String removeFormat = "attachmentsEditor.removeAttachment('" + appName + "', '"+ module + "', this, '%s')";
%>
<input id="<%=propertyKey%>" name="<%=propertyKey%>" type="hidden" value="<%=attachmentsId%>"/>

<div class="ox-attachments" id="xava_attachments_<%=attachmentsId%>">
<a style="display: none" class="ox-attachment-item" href='<%=String.format(hrefFormat, "")%>' target="_blank" tabindex="1">	
</a>
<%
Collection<AttachmentVO> attachments = Attachment.getAttachmentsData(attachmentsId);
for (AttachmentVO attachment : attachments) {
	String data = "data-attachment='" + attachment.name + ":" + attachment.size + "B:" + attachment.type + "'";
%>
<a class="ox-attachment-item" href='<%=String.format(hrefFormat, attachment.id)%>' <%=data%> target="_blank" tabindex="1">
	<%=attachment.name%>
</a>
<%if (editable) {%>
<a title='<xava:label key="Attachments.remove"/>' href="javascript:void(0)" class='<%=style.getActionImage()%>'>
	<i class="mdi mdi-delete" onclick="<%=String.format(removeFormat, attachment.id)%>"></i>
</a>		
<%} %>
&nbsp;&nbsp;
<%} %>
</div>
<div class="ox-attachments-actions">	
<% if (editable) { 
	Long chunkSize = "".equals(request.getParameter("chunkSize")) ? 10240L : Long.parseLong(request.getParameter("chunkSize"));
	String accept =  "".equals(request.getParameter("accept")) ? "" : "accept=\"" + request.getParameter("accept") + "\"";
%>                  
<label class="ox-upload-attachments">
    <i class="mdi mdi-attachment"></i>
	<input type="file" class="ox_new_attachments" id="<%=attachmentsId%>" tabindex="1" <%=accept%> multiple  
		onchange="for (var i = 0; i < this.files.length; i++) attachmentsEditor.upload('<%=appName%>','<%=module%>',<%=chunkSize%>,this.files[i],'<%=attachmentsId%>');" >
</label>	 
<xava:action action='Attachments.removeAll' argv='<%="newAttachmentsProperty=" + Ids.undecorate(propertyKey)%>'/>	    	
<% } %>	
</div>

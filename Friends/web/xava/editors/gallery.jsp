<%@ include file="../imports.jsp"%>

<%@ page import="java.util.Iterator" %>
<%@ page import="org.openxava.util.Strings" %>
<%@ page import="org.openxava.session.GalleryImage" %>

<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
final int IMAGES_BY_ROW = 6;
final int MINIMIZED_SIZE = 100;
org.openxava.session.Gallery gallery = (org.openxava.session.Gallery) context.get(request, "xava_gallery");
String applicationName = request.getParameter("application");
String module = request.getParameter("module");
long dif=System.currentTimeMillis(); // to avoid browser caching
%>

<div class="<%=style.getImagesGallery()%>">  

<%=style.getFrameHeaderStartDecoration()%>
<%=style.getFrameTitleStartDecoration()%>
<%=gallery.getTitle()%>
<%=style.getFrameTitleEndDecoration()%>
<%=style.getFrameActionsStartDecoration()%>
<% if (gallery.isMaximized()) { %>
<xava:action action='Gallery.minimizeImage'/>
<% } %>
<%=style.getFrameActionsEndDecoration()%>
<%=style.getFrameHeaderEndDecoration()%>
<%=style.getFrameContentStartDecoration()%>	
	
<%
if (gallery.isMaximized()) {		
%>
<xava:link action='Gallery.minimizeImage'>
<img src='<%=request.getContextPath()%>/xava/gallery?application=<%=applicationName%>&module=<%=module%>&oid=<%=gallery.getMaximizedOid()%>&dif=<%=dif%>' border="0"/>	
</xava:link>
<%
}
else {
%>
<table>
<tr>

<%
	int c = 0;
	for (Iterator it = gallery.getImages().iterator(); it.hasNext(); ) {
		GalleryImage image = (GalleryImage) it.next();
		String removeImage=!style.getRemoveImage().startsWith("/")?request.getContextPath() + "/" + style.getRemoveImage():style.getRemoveImage(); 
		if (c++ % IMAGES_BY_ROW == 0) {
%>
</tr><tr>
<%
		}
%>
<td>	
	<%=style.getFrameHeaderStartDecoration()%>			
	<%=style.getFrameTitleStartDecoration()%>
	&nbsp;
	<%=style.getFrameTitleEndDecoration()%>
	<%=style.getFrameActionsStartDecoration()%>
	<xava:action action='Gallery.maximizeImage' argv='<%="oid=" + image.getOid()%>'/>
	<% if (!gallery.isReadOnly()) { %>
	<xava:action action='Gallery.removeImage' argv='<%="oid=" + image.getOid()%>'/>
	<% } %>	
	<%=style.getFrameActionsEndDecoration()%>
	<%=style.getFrameHeaderEndDecoration()%>
	<%=style.getFrameContentStartDecoration()%>
	<input type="hidden" name="xava.GALLERY.images" value="<%=image.getOid()%>">
	<xava:link action='Gallery.maximizeImage' argv='<%="oid=" + image.getOid()%>'>
	<img src='<%=request.getContextPath()%>/xava/gallery?application=<%=applicationName%>&module=<%=module%>&oid=<%=image.getOid()%>&dif=<%=dif%>'
		width="<%=MINIMIZED_SIZE%>" height="<%=MINIMIZED_SIZE%>" border="0"/>
	</xava:link>		
	<%=style.getFrameContentEndDecoration()%>
</td>
<%
	} // for
%>
</tr>
</table>
<%
} // if maximized
%>
<%-- The next line is for giving enough width for showing the heading in one line --%>
<% if (!gallery.isMaximized()) { %>
	<%=Strings.repeat(gallery.getTitle().length()*2+10, "&nbsp;")%>
<% } %>
<%=style.getFrameContentEndDecoration()%>


</div>    

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
String frameId=request.getParameter("frameId");
boolean closed="true".equals(request.getParameter("closed"));
String frameContentId=frameId + "content";
String frameShowId=frameId + "show";
String frameHideId=frameId + "hide";
String hideClass=closed?"class='ox-display-none'":"";
String showClass=closed?"":"class='ox-display-none'";

String minimizeImage = null;
if (style.getMinimizeImage() != null) minimizeImage=!style.getMinimizeImage().startsWith("/")?request.getContextPath() + "/" + style.getMinimizeImage():style.getMinimizeImage();
String restoreImage = null;
if (style.getRestoreImage() != null) restoreImage=!style.getRestoreImage().startsWith("/")?request.getContextPath() + "/" + style.getRestoreImage():style.getRestoreImage();
%> 		

<span id="<%=frameHideId%>" <%=hideClass%>>
	<%-- tmr
	<a href="javascript:openxava.hideFrame('<%=frameId%>')">
	--%>
	<%-- tmr ini --%>
	<a class="xava_hide_frame" data-frame="<%=frameId%>">
	<%-- tmr fin --%>
		<% if (minimizeImage == null) { %>
		<i class="mdi mdi-menu-down"></i>
		<% } else { %>
		<img src="<%=minimizeImage%>" border=0 align="absmiddle"/>
		<% } %>
	</a>
</span> 
<span id="<%=frameShowId%>" <%=showClass%>>
	<%-- tmr
	<a href="javascript:openxava.showFrame('<%=frameId%>')">
	--%>
	<%-- tmr ini --%>
	<a class="xava_show_frame" data-frame="<%=frameId%>">
	<%-- tmr fin --%>	
		<% if (restoreImage == null) { %>
		<i class="mdi mdi-menu-right"></i>
		<% } else { %>
		<img src="<%=restoreImage%>" border=0 align="absmiddle"/>
		<% } %>
	</a>
</span>

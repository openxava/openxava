<%@ include file="imports.jsp"%>

<%@page import="org.openxava.util.XavaPreferences"%>
<%@page import="org.openxava.util.Is"%>
<%@page import="org.openxava.controller.meta.MetaControllers"%>
<%@page import="org.openxava.controller.meta.MetaAction"%>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
boolean showImages = style.isShowImageInButtonBarButton();
boolean showIcons = style.isUseIconsInsteadOfImages(); 
boolean showLabels = !showImages?true:XavaPreferences.getInstance().isShowLabelsForToolBarActions();
String actionName = request.getParameter("action");
String addSpaceWithoutImage = request.getParameter("addSpaceWithoutImage");
boolean addSpace = "true".equals(addSpaceWithoutImage);
if (!Is.emptyString(actionName)) {
	MetaAction action = MetaControllers.getMetaAction(request.getParameter("action"));
	String argv = request.getParameter("argv");
	String label = action.getLabel(request); 
%>

	<% if (style.isUseStandardImageActionForOnlyImageActionOnButtonBar() && action.hasImage() && Is.emptyString(label)) { %>
<xava:image action='<%=action.getQualifiedName()%>' argv='<%=argv %>' cssClass='<%=style.getButtonBarImage()%>'/>	
	<% } else {  %>		
<span class="<%=style.getButtonBarButton()%>">	
<xava:link action="<%=action.getQualifiedName()%>" argv='<%=argv %>'>
		<% 
		boolean showLabel = (showLabels || !action.hasImage()) && !Is.emptyString(label);  
		boolean showImage = showImages && action.hasImage() || action.hasImage() && Is.emptyString(label);
		boolean showIcon = action.hasIcon() && (showImages && (showIcons || !action.hasImage()) || Is.emptyString(label) && (showIcons || !action.hasImage())); 
		%>
		<% if (showIcon) { %>
		<i class="mdi mdi-<%=action.getIcon()%>"></i>
		<% } else if (showImage) { %>
		<span style="background: url(<%=request.getContextPath()%>/<%=style.getImagesFolder()%>/<%=action.getImage()%>) no-repeat 5px 50%;">
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span>		
		<% } else if(addSpace) {%>
		<span>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</span>
		<%
		}
		if (showLabel) { %>			 				 			
		<%=label%>
		<% } %>		
	</xava:link>
	<% } %>	
</span>
<%
}
%>
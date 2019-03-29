<%@page import="org.openxava.controller.meta.MetaControllers"%>
<%@page import="org.openxava.controller.meta.MetaController"%>
<%@page import="org.openxava.util.Is"%>
<%@page import="org.openxava.web.Ids"%>
<%@page import="org.openxava.util.Labels"%>
<%@page import="java.util.Collection"%>
<%@page import="org.openxava.controller.meta.MetaAction"%>
<%@page import="org.openxava.controller.meta.MetaSubcontroller"%>

<%@ include file="imports.jsp"%>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>
<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<%
org.openxava.controller.ModuleManager manager = (org.openxava.controller.ModuleManager) context.get(request, "manager", "org.openxava.controller.ModuleManager");
manager.setSession(session);
String controllerName = request.getParameter("controller");
MetaController metaController = MetaControllers.getMetaController(controllerName);
String image = request.getParameter("image");
if (Is.empty(image)) image = metaController.getImage();
String icon = request.getParameter("icon"); 
if (Is.empty(icon)) icon = metaController.getIcon();
String mode = request.getParameter("xava_mode"); 
if (mode == null) mode = manager.getModeName();
String argv = request.getParameter("argv");
if (Is.empty(argv)) argv = "";
// add the mode in the ids to fix problem on the split mode
String id = Ids.decorate(request, "sc-" + controllerName + "_" + mode);
String containerId = Ids.decorate(request, "sc-container-" + controllerName + "_" + mode);
String buttonId = Ids.decorate(request, "sc-button-" + controllerName + "_" + mode);
String imageId = Ids.decorate(request, "sc-image-" + controllerName + "_" + mode);
String aId = Ids.decorate(request, "sc-a-" + controllerName + "_" + mode);
String spanId = Ids.decorate(request, "sc-span-" + controllerName + "_" + mode);
%>
<span id='<%=containerId%>'>
	<span id='<%=buttonId%>' class="<%=style.getButtonBarButton()%> <%=style.getSubcontrollerButton()%>">
		<a 
			id ='<%=aId%>'
			href="javascript:openxava.subcontroller('<%=id%>','<%=containerId%>','<%=buttonId%>','<%=imageId%>','<%=aId%>','<%=spanId%>')" 
			>
			<% if (!Is.emptyString(icon) && (style.isUseIconsInsteadOfImages() || Is.emptyString(image))) { %>
			<i class="mdi mdi-<%=icon%>"></i>
			<% } else { %>
			<span
				id='<%=spanId%>' 
				style="padding:4px; background: url(<%=request.getContextPath()%>/<%=style.getImagesFolder()%>/<%=image%>) no-repeat 5px 50%;">				
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			</span>
			<% } %>
			<%= Labels.get(controllerName)%>
			<i id='<%=imageId%>' class="mdi mdi-menu-down"></i>&nbsp;
		</a>
	</span>
	
	<div id="<%=id%>" class="<%=style.getSubcontroller()%>" style="display:none;">
		<table>
		<%
		Collection actions = manager.getSubcontrollerMetaActions(controllerName);
		java.util.Iterator actionsIt = actions.iterator();
		while(actionsIt.hasNext()){
			MetaAction action = (MetaAction)actionsIt.next();
			if (action.appliesToMode(mode)) {
		%>	
			<tr><td>
				<jsp:include page="barButton.jsp">
					<jsp:param name="action" value="<%=action.getQualifiedName()%>"/>
					<jsp:param name="addSpaceWithoutImage" value="true"/>
					<jsp:param name="argv" value='<%=argv%>'/>
				</jsp:include>
			</td></tr>
		<%
			}
		}
		%>
		</table>
	</div>
</span>	
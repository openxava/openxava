<%@page import="org.openxava.util.Users"%>

<%@include file="../xava/imports.jsp"%>

<jsp:useBean id="modules" class="com.openxava.naviox.Modules" scope="session"/>

<div id="modules_list"> 

	<div id="modules_list_top"> 

		<div id="application_title">
		
			<div id="application_name">
				<%=modules.getApplicationLabel(request)%>
			</div>
		
			<div id="organization_name">
				<% String organizationName = modules.getOrganizationName(request); %>
				<%=organizationName%>
				<%@ include file="organizationNameExt.jsp"%>
			</div>
		
		</div>
		
		<% if (Users.getCurrent() != null && modules.showsIndexLink()) { %>
			 
			<a href="<%=request.getContextPath()%>/m/Index">
				<div id='organizations_index' class='<%="Index".equals(request.getParameter("module"))?"selected":""%>'>	
					<i class="mdi mdi-apps"></i>
					<xava:label key="myOrganizations"/>
				</div>	
			</a>
			
		<% } %>
	
		<jsp:include page="selectModules.jsp">
			<jsp:param name="fixedModules" value="true"/>
		</jsp:include>
		
		
		<jsp:include page="selectModules.jsp">
			<jsp:param name="bookmarkModules" value="true"/>
		</jsp:include>
		
		<% if (modules.showsSearchModules(request)) { %>
		<div id="search_modules">
		<input id="search_modules_text" type="text" size="38" placeholder='<xava:message key="search_modules"/>'/>
		</div>
		<% } %>
		
	</div> 	
							
	<div id="modules_list_outbox">
		<table id="modules_list_box">
			<tr id="modules_list_content">
				<td>
					<jsp:include page="modulesMenu.jsp"/>
				</td>						
			</tr>
		</table>
	</div>

</div>

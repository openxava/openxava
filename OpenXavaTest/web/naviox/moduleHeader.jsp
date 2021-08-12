<%-- tmp --%>

<%-- TMP ME QUEDÉ POR AQUÍ: REFACTORIZANDO --%>
			
<div id="module_header_left"> 
			
				<% if (!isFirstSteps) { %>
				<a id="module_header_menu_button" href="javascript:naviox.showModulesList('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>')">
					<i class="mdi mdi-menu"></i></a>
				<% } %>
				<%-- tmp	
				<span id="module_title">
					<%
					if (hasModules && !isFirstSteps) {
					%>
					<span id="module_extended_title">
						<%
						String organizationName = modules.getOrganizationName(request);
						if (!Is.emptyString(organizationName)) {
						%> 
						<%=organizationName%> - 
						<%
						}
						%>						
						<%=modules.getApplicationLabel(request)%> -
					</span>	 
					<%
					}
					%>
					
					<%String moduleTitle = hasModules?modules.getCurrentModuleLabel():modules.getCurrentModuleDescription(request);%>
					<%=moduleTitle%>
				</span>	
				<a href="javascript:naviox.bookmark()" title="<xava:message key='<%=modules.isCurrentBookmarked(request)?"unbookmark_module":"bookmark_module"%>'/>"> 
					<i id="bookmark" class='mdi mdi-star<%=modules.isCurrentBookmarked(request)?"":"-outline"%>'></i> 
				</a>
				--%>
				
				<%-- tmp ini --%>
				<%
				if (hasModules && !isFirstSteps) {
				%>
				<span id="module_extended_title">
					<%
					String organizationName = modules.getOrganizationName(request);
					if (!Is.emptyString(organizationName)) {
					%> 
					<%=organizationName%> - 
					<%
					}
					%>						
					<%=modules.getApplicationLabel(request)%> :
				</span>	 
				<%
				}
				%>
								
				<% 
				for (MetaModule metaModule: modules.getTopModules()) {
					if (metaModule.getName().equals("SignIn")) continue;
					if (modules.showsIndexLink() && metaModule.getName().equals("Index")) continue;
					String selected = metaModule.getName().equals(request.getParameter("module"))?"selected":"unselected";
				%>		
					<a href="<%=modules.getModuleURI(request, metaModule)%>?retainOrder=true" class="<%=selected%>"><%=metaModule.getLabel(request.getLocale())%></a>
				<%
				}
				%>				
				</div>
				
				<div id="module_header_right">
				
				<a href="javascript:naviox.bookmark()" title="<xava:message key='<%=modules.isCurrentBookmarked(request)?"unbookmark_module":"bookmark_module"%>'/>"> 
					<i id="bookmark" class='mdi mdi-star<%=modules.isCurrentBookmarked(request)?"":"-outline"%>'></i> 
				</a>
				 				
				<%-- tmp fin --%>
				
				<span id="sign_in_out"> <%-- tmp era div --%>
					<%
					if (Is.emptyString(NaviOXPreferences.getInstance().getAutologinUser())) {
						String userName = Users.getCurrent();
						String currentModule = request.getParameter("module");
						boolean showSignIn = userName == null && !currentModule.equals("SignIn");						
						if (showSignIn) {
							String selected = "SignIn".equals(currentModule)?"selected":"";
					%>
					<a href="<%=request.getContextPath()%>/m/SignIn" class="sign-in <%=selected%>">
							<xava:message key="signin"/>
					</a>
					<%
						}
						if (userName != null) {
							String organization = Organizations.getCurrent(request);
							if (organization == null) organization = "";
					%>
					<a  href="<%=request.getContextPath()%>/naviox/signOut.jsp?organization=<%=organization%>" class="sign-in"><xava:message key="signout"/> (<%=userName%>)</a>
					<%
						}
					} 
					%>
				</span>
				
				<%-- tmp ini --%>				
				</div>
				<%-- tmp fin --%>
	
<%@ include file="imports.jsp"%>

<%@page import="org.openxava.util.Strings"%>
<%@page import="org.openxava.web.style.Themes"%>

<div id="theme_chooser">
	<xava:label key="themes"/>:
	<% 
	String nexus = "";
	String current = Themes.getCSS(request);
	for (String theme: Themes.getAll()) {
		String label = Strings.firstUpper(theme.replace(".css", "").replace("-", " "));
		if (theme.equals(current)) {
		%>
			<%=nexus%><b><%=label%></b> 
		<% 			
		}
		else {
		%>
			<%=nexus%><a href="?theme=<%=theme%>"><%=label%></a> 
		<% 	
		}
		nexus = " - ";
	} 
	%>
</div>
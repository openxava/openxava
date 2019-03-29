<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="org.openxava.util.Labels" %>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
String propertyKey = request.getParameter("propertyKey");
String [] fvalues = (String []) request.getAttribute(propertyKey + ".fvalue");
boolean editable="true".equals(request.getParameter("editable"));
String disabled=editable?"":"disabled";
String script = request.getParameter("script");
boolean label = org.openxava.util.XavaPreferences.getInstance().isReadOnlyAsLabel();
if (editable || !label) {
	String sregionsCount = request.getParameter("regionsCount");
	int regionsCount = sregionsCount == null?5:Integer.parseInt(sregionsCount);
	Collection regions = fvalues==null?Collections.EMPTY_LIST:Arrays.asList(fvalues);
%>
<select name="<%=propertyKey%>" tabindex="1" multiple="multiple" 	
	class=<%=style.getEditor()%> 
	<%=disabled%>
	<%=script%>>
	<%
	for (int i=1; i<regionsCount+1; i++) {
		String selected = regions.contains(Integer.toString(i))?"selected":"";		
	%>	
	<option value="<%=i%>" <%=selected%>><%=Labels.get("regions." + i, request.getLocale())%></option>
	<%
	}
	%>
</select>
<%
}
else {
	for (int i=0; i<fvalues.length; i++) {
%>
<%=Labels.get("regions." + fvalues[i], request.getLocale())%>
<%
	}
}
%>

<% 
if (!editable) { 
	for (int i=0; i<fvalues.length; i++) {
%>
		<input type="hidden" name="<%=propertyKey%>" value="<%=fvalues[i]%>">		
<%
	}
} 
%>			

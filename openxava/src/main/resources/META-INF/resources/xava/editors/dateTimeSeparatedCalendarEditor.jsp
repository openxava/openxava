<%-- WARNING: IF YOU CHANGE THIS CODE PASS THE MANUAL TEST ON DateCalendarTest.txt --%>

<%@ page import="org.openxava.model.meta.MetaProperty" %>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String [] fvalues = (String []) request.getAttribute(propertyKey + ".fvalue");
String fDate = fvalues[0];
String fTime = fvalues[1];
String fvalue = fDate + " " + fTime;
String align = p.isNumber()?"right":"left";
boolean editable="true".equals(request.getParameter("editable"));
String disabled=editable?"":"disabled";
boolean label = org.openxava.util.XavaPreferences.getInstance().isReadOnlyAsLabel();
String browser = request.getHeader("user-agent");
String size = browser.contains("Chrome")?"10":"12";
if (editable || !label) {
	String dateClass = editable?"xava_date":""; 
%>
<span class="<%=dateClass%> ox-date-calendar" data-date-format="<%=org.openxava.util.Dates.dateFormatForJSCalendar()%>">
<input type="text" name="<%=propertyKey%>" id="<%=propertyKey%>" class="<%=style.getEditor()%>" title="<%=p.getDescription(request)%>"
	tabindex="1" 
	align='<%=align%>'
	maxlength="10"
	data-input
	size="<%=size%>" 
	value="<%=fDate%>" <%=disabled%>>
	<%if (editable) {%>
	<a data-toggle>
	<i class="mdi mdi-calendar"></i>
	</a>
	<%} %>
</span>
<jsp:include page="timeCalendarEditor.jsp">
		<jsp:param name="timeCalendarName" value="<%=propertyKey%>" />
		<jsp:param name="timeCalendarAlign" value="<%=align%>" />
		<jsp:param name="timeCalendarFTime" value="<%=fTime%>" />
		<jsp:param name="timeCalendarDisabled" value="<%=disabled%>" />
	</jsp:include>
<%
} else {
%>
<%=fvalue%>&nbsp;	
<%
}
%>
<% if (!editable) { %>
	<input type="hidden" name="<%=propertyKey%>" value="<%=fvalue%>">
<% } %>			

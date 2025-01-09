<%-- WARNING: IF YOU CHANGE THIS CODE PASS THE MANUAL TEST ON DateCalendarTest.txt --%>

<%@ page import="org.openxava.model.meta.MetaProperty" %>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>
  
<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
String align = p.isNumber()?"right":"left";
boolean editable="true".equals(request.getParameter("editable"));
String disabled=editable?"":"disabled";
boolean label = org.openxava.util.XavaPreferences.getInstance().isReadOnlyAsLabel();
if (editable || !label) {
	String dateClass = editable?"xava_date":""; 
%>
<span class="<%=dateClass%> ox-date-calendar" 
	data-date-format="<%=org.openxava.util.Dates.dateTimeFormatForJSCalendar()%>"
	data-enable-time="true">
	<input type="text" name="<%=propertyKey%>" id="<%=propertyKey%>" class="<%=style.getEditor()%>" title="<%=p.getDescription(request)%>"
		tabindex="1" 
		align='<%=align%>'
		maxlength="19"
		data-input
		size="19" 
		value="<%=fvalue%>" <%=disabled%>><%if (editable) {%><a data-toggle><i class="mdi mdi-calendar-clock"></i></a><%} %>	
</span>

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

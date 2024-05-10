<%-- WARNING: IF YOU CHANGE THIS CODE PASS THE MANUAL TEST ON DateCalendarTest.txt --%>

<%@ page import="org.openxava.model.meta.MetaProperty" %>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>
  
<%
String propertyKey;
MetaProperty p;
boolean editable;
String title;
String align;
String fvalue;
String disabled;

if (request.getParameter("timeCalendarName") != null) {
  propertyKey = request.getParameter("timeCalendarName");
  p = (MetaProperty) request.getAttribute(propertyKey);
  editable = "true".equals(request.getParameter("editable"));
  align = request.getParameter("timeCalendarAlign");
  fvalue = request.getParameter("timeCalendarFTime");
  disabled = request.getParameter("timeCalendarDisabled");
} else {
  propertyKey = request.getParameter("propertyKey");
  p = (MetaProperty) request.getAttribute(propertyKey);
  editable = "true".equals(request.getParameter("editable"));
  align = p.isNumber() ? "right" : "left";
  fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
  disabled = editable ? "" : "disabled";
}
boolean label = org.openxava.util.XavaPreferences.getInstance().isReadOnlyAsLabel();
String browser = request.getHeader("user-agent");
int sizeIncrement = browser.contains("Chrome")?0:2; 
String dateClass = editable?"xava_time":""; 

if (editable || !label) {
	
%>
<span class="<%=dateClass%> ox-date-calendar" data-date-format="<%=org.openxava.util.Dates.timeFormatForJSCalendar()%>">
<input type="text" name="<%=propertyKey%>" id="<%=propertyKey%>" class="<%=style.getEditor()%>" title="<%=p.getDescription(request)%>"
	tabindex="1" 
	align='<%=align%>'
	maxlength="8"
	data-input
	size="8" 
	value="<%=fvalue%>" <%=disabled%>>
	<%if (editable) {%><a data-toggle><i class="mdi mdi-clock-outline"></i></a><%} %>
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

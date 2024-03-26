<%-- WARNING: IF YOU CHANGE THIS CODE PASS THE MANUAL TEST ON DateCalendarTest.txt --%>

<%@ page import="org.openxava.model.meta.MetaProperty" %>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>
  
<%
String propertyKey = request.getParameter("timeCalendarName") != null ? request.getParameter("timeCalendarName") : request.getParameter("propertyKey");

MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
boolean editable="true".equals(request.getParameter("editable"));

String title =  request.getParameter("timeCalendarTitle") != null ? request.getParameter("timeCalendarTitle") : "";
String align = request.getParameter("timeCalendarAlign") != null ? request.getParameter("timeCalendarAlign") : p.isNumber()?"right":"left";
String fvalue = request.getParameter("timeCalendarFTime") != null ? request.getParameter("timeCalendarFTime") : (String) request.getAttribute(propertyKey + ".fvalue");
String disabled= request.getParameter("timeCalendarDisabled") != null ? request.getParameter("timeCalendarDisabled") : editable?"":"disabled";

//String propertyKey = request.getParameter("propertyKey");
//MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
//String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
//String align = p.isNumber()?"right":"left";
//boolean editable="true".equals(request.getParameter("editable"));
//String disabled=editable?"":"disabled";
boolean label = org.openxava.util.XavaPreferences.getInstance().isReadOnlyAsLabel();
String browser = request.getHeader("user-agent");
int sizeIncrement = browser.contains("Chrome")?0:2; 
if (editable || !label) {
	String dateClass = editable?"xava_time":""; 
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

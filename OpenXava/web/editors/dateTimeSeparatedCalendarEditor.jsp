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
String script = request.getParameter("script");
boolean label = org.openxava.util.XavaPreferences.getInstance().isReadOnlyAsLabel();
String browser = request.getHeader("user-agent");
String size = browser.contains("Chrome")?"10":"12";
if (editable || !label) {
	String dateClass = editable?"xava_date":""; 
%>
<span class="<%=dateClass%> <%=style.getDateCalendar()%>" data-date-format="<%=org.openxava.util.Dates.dateFormatForJSCalendar()%>">
<input type="text" name="<%=propertyKey%>" id="<%=propertyKey%>" class="<%=style.getEditor()%>" title="<%=p.getDescription(request)%>"
	tabindex="1" 
	align='<%=align%>'
	maxlength="10"
	data-input
	size="<%=size%>" 
	value="<%=fDate%>" <%=disabled%> <%=script%>><%if (editable) {%><a href="javascript:void(0)" data-toggle style="position: relative; right: 25px;"><i class="mdi mdi-calendar"></i></a><%} %> <input name="<%=propertyKey%>" class=<%=style.getEditor()%>	
	type="text" 
	title="<%=p.getDescription(request)%>"
	tabindex="1"
	align='<%=align%>'
	maxlength="8" 
	size="8" 
	value="<%=fTime%>"
	<%=disabled%>
	<%=script%>/>	
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

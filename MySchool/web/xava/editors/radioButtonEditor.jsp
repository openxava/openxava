<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/> 
 
<%@ include file="validValueEditorCommon.jsp"%>

<% 
if (editable) {
%>
<div class="<%=style.getRadioButtons()%>">
<% 
java.util.Iterator it = p.validValuesLabels(request); 
for (int i = baseIndex; it.hasNext(); i++) { 
String selected = value == i ?"checked":""; 
%> 
 
<input type="radio" name="<%=propertyKey%>" tabindex="1" value="<%=i%>" <%=script%> <%=selected%>> <%=it.next()%> &nbsp;&nbsp;

<%
String horizontal = request.getParameter("horizontal");
Boolean isHorizontal = Boolean.valueOf(horizontal);
%>

<% if (!isHorizontal.booleanValue()) %> <br> <% ; %>

<% 
} // while 
%> 
 </div> 
<%  
} else {  
description = p.getValidValueLabel(request, value);  
if (label) { 
%> 
<%=description%> 
<% 
} 
else { 
%> 
 
<% } %> 
 
<% } %> 
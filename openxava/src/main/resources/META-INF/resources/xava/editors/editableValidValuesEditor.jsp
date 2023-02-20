<%@page import="java.util.Map"%>
<%@page import="org.openxava.model.meta.MetaProperty"%>
<%@page import="org.openxava.view.View"%>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>
<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<%
String viewObject = request.getParameter("viewObject");
View view = (View) context.get(request, viewObject);
String collectionName = request.getParameter("collectionName");
if (!org.openxava.util.Is.emptyString(collectionName)) {
	view = view.getSubview(collectionName);
}
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String script = request.getParameter("script"); 
boolean editable = "true".equals(request.getParameter("editable")); 
boolean label = org.openxava.util.XavaPreferences.getInstance().isReadOnlyAsLabel();
Object value = request.getAttribute(propertyKey + ".value") == null ? "" : request.getAttribute(propertyKey + ".value");
Map<Object, Object> validValues = view.getValidValues(p.getName());
Object description = validValues.get(value);
%>

<%
if (editable) { 
%>

        
<input list="<%=propertyKey%>__LIST__" id="<%=propertyKey%>" name="<%=propertyKey%>" value="<%=value%>" title="<%=p.getDescription(request)%>">
<datalist id="<%=propertyKey%>__LIST__">
<%
	for (Map.Entry e: validValues.entrySet()) {
        
		String selected = e.getKey().equals(value) ?"selected":"";
        //System.out.println(propertyKey);
%>
    <option value="<%=e.getKey()%>" <%=selected%>> <%=e.getValue()%> </option>
<%  
	} // while
%>
	
</datalist>
<input type="hidden" name="<%=propertyKey%>__DESCRIPTION__" value="<%=description%>"/>
<% 
} else { 
	if (label) {
%>
	<%=description%>
<%
	}
	else {
%>
	<input name = "<%=propertyKey%>_DESCRIPTION_" class=<%=style.getEditor()%>
	type="text" 
	title="<%=p.getDescription(request)%>"	
	maxlength="<%=p.getSize()%>" 	
	size="<%=p.getSize()%>" 
	value="<%=description%>"
	disabled
	/>
<%  } %>
	<input type="hidden" name="<%=propertyKey%>" value="<%=value%>">	
<% } %>			

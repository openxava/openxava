<%@ include file="../imports.jsp"%>

<%@page import="java.util.Map"%>
<%@page import="org.openxava.model.meta.MetaProperty"%>
<%@page import="org.openxava.view.View"%>
<%@page import="java.util.Collections"%>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>
<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>

<%
String viewObject = request.getParameter("viewObject");
View view = (View) context.get(request, viewObject);
String collectionName = request.getParameter("collectionName");
String contextPath = (String) request.getAttribute("xava.contextPath");
if (contextPath == null) contextPath = request.getContextPath();
if (!org.openxava.util.Is.emptyString(collectionName)) {
	view = view.getSubview(collectionName);
}
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String script = request.getParameter("script");
/* tmr 
String scriptSelect = script; 
String scriptInput = script;
if (script.contains("onchange=")){
	String selectOnChange = "editableValidValuesEditor.handleSelectChange(this), ";
    String inputOnChange= "editableValidValuesEditor.handleSelectInput(this), ";
    int i = script.indexOf("onchange=") + 10;
    scriptSelect = script.substring(0,i) + selectOnChange + script.substring(i);
    scriptInput = script.substring(0,i) + inputOnChange + script.substring(i);
} 
*/
boolean editable = "true".equals(request.getParameter("editable")); 
boolean label = org.openxava.util.XavaPreferences.getInstance().isReadOnlyAsLabel();
Object value = request.getAttribute(propertyKey + ".value") == null ? "" : request.getAttribute(propertyKey + ".value");
Map<Object, Object> validValues = view.getValidValues(p.getName()) == null ? Collections.emptyMap(): view.getValidValues(p.getName()) ;
Object description = validValues.get(value);
String optionHidden = new String(new char[p.getSize()]).replace("\0", "A");
String firstOption = "";
if (value != null && !validValues.isEmpty() && !validValues.containsValue(value)){
    firstOption = value.toString();
}
%>
    
<%
if (editable) { 
   	if (validValues.isEmpty()) {
%>
	<input id="<%=propertyKey%>" name="<%=propertyKey%>" class=<%=style.getEditor()%> type="text" tabindex="1" maxlength="<%=p.getSize()%>" size="<%=p.getSize()%>" value="<%=value%>" <%=script%> title="<%=p.getDescription(request)%>"/>
<%
	} else {
%>
    <div class="ox-select-editable">
    	<%-- tmr
    	<select tabindex="1" class=<%=style.getEditor()%> title="<%=p.getDescription(request)%>" <%=scriptSelect%> onchange="editableValidValuesEditor.handleSelectChange(this)">
    	--%>
    	<%-- tmr ini --%>
    	<select tabindex="1" class=<%=style.getEditor()%> title="<%=p.getDescription(request)%>">
    	<%-- tmr fin --%>
<% 
		if (view.hasBlankValidValue(p.getName())) { 
%>
			<option value="<%=firstOption%>"><%=firstOption%></option>
<% 
		} 
		for (Map.Entry e: validValues.entrySet()) {
			String selected = e.getKey().equals(value) ?"selected":"";
%>
			<option value="<%=e.getKey()%>" <%=selected%>> <%=e.getValue()%> </option>
<%
		}
%>      
        <option hidden><%=optionHidden%></option>
		</select>
		<%-- tmr 
		<input id="<%=propertyKey%>" name="<%=propertyKey%>" type="text"  <%=scriptInput%> onchange="editableValidValuesEditor.handleSelectInput(this)" maxlength="<%=p.getSize()%>" size="<%=p.getSize()%>" value="<%=value%>"/>
		--%>
		<%-- tmr ini --%>
		<input id="<%=propertyKey%>" name="<%=propertyKey%>" type="text" maxlength="<%=p.getSize()%>" size="<%=p.getSize()%>" value="<%=value%>"/>		
		<%-- tmr fin --%>
		<input type="hidden" name="<%=propertyKey%>__DESCRIPTION__" value="<%=description%>"/>
	</div>
<%		
	}
} else { 
	if (label) {
%>
	<%=description%>
<%
	} else {
%>
	<input name = "<%=propertyKey%>_DESCRIPTION_" class=<%=style.getEditor()%> type="text" title="<%=p.getDescription(request)%>" maxlength="<%=p.getSize()%>" size="<%=p.getSize()%>" value="<%=description%>" disabled/>
<%  
	} 
%>
	<input type="hidden" name="<%=propertyKey%>" value="<%=value%>">	
<% 
} 
%>
<%-- tmr		
<script type="text/javascript" <xava:nonce/> src="<%=contextPath%>/xava/editors/js/editableValidValuesEditor.js"></script>
--%>
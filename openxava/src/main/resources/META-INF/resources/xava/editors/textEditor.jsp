<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.DecimalFormatSymbols" %>
<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.util.Strings" %>
<%@ page import="org.openxava.util.Align" %>
<%@ page import="org.openxava.util.Locales" %>
<%@ page import="org.openxava.util.XavaResources" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>
<%@ page import="org.apache.commons.logging.Log" %>

<%@ page import="java.util.Enumeration" %>

<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
String align = p.isNumber()?"ox-text-align-right":"";
boolean editable="true".equals(request.getParameter("editable"));
String disabled=editable?"":"disabled";
boolean label = org.openxava.util.XavaPreferences.getInstance().isReadOnlyAsLabel();
String inputType = request.getParameter("inputType");
if (inputType == null) inputType = "text"; 
String smaxSize = request.getParameter("maxSize");
int maxSize = 0;
if (!org.openxava.util.Is.emptyString(smaxSize)) {
	maxSize = Integer.parseInt(smaxSize);
}
else {
	maxSize = org.openxava.util.XavaPreferences.getInstance().getMaxSizeForTextEditor();
}
int size = p.getSize() > maxSize?maxSize:p.getSize();
int maxLength = p.getSize();
String numericAlt = ""; 
String numericClass = ""; 
if (p.isNumber()) {
	if (p.getScale() > 0) {
		int sizeIncrement = (size - 1) / 3 + 2; // The points/commas for thousands + point/comma for decimal + minus sign
		size += sizeIncrement;
		maxLength += sizeIncrement;
	}
	String integer = p.getScale() == 0?"true":"false";
	numericAlt = getNumericAlt(p.getSize(), p.getScale()); 
	numericClass = "xava_numeric"; 
}	
    
boolean fillWithZeros = "true".equals(request.getParameter("fillWithZeros"));
if (fillWithZeros && fvalue.length() > 0) {	
	fvalue = Strings.fix(fvalue, size, Align.RIGHT, '0');
}
    
String im = (request.getParameter("value") != null) && (request.getParameter("value").toString().matches("[-AL0!@#$%^&*()_+={}';:\"<>.,?/` \\~]+")) ? request.getParameter("value") : "";
if (im.length() > 1) {
    size = im.length();
    maxLength= im.length();
    im = "data-inputmask=\"'mask': '" + im + "'\"";
}

boolean email = "true".equals(request.getParameter("email"));
if (email) {
	im = "data-inputmask=\"'mask': '*{1,20}[.*{1,20}][.*{1,20}][.*{1,20}]@*{1,20}[.*{1,10}][.*{1,2}]'\"";
}
    
if (editable || !label) { 
%>
<input id="<%=propertyKey%>"
    name="<%=propertyKey%>" class="<%=style.getEditor()%> <%=numericClass%> <%=align%>"
	type="<%=inputType%>" 
	tabindex="1"
	title="<%=p.getDescription(request)%>"
	maxlength="<%=maxLength%>" 
	size="<%=size%>"
	<%=numericAlt%> 
	value="<%=Strings.change(fvalue, "\"", "&quot;")%>"	
	<%=disabled%>	
    <%=im%>
	/>
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


<%!
private static Log log = LogFactory.getLog("textEditor.jsp");

private String getNumericAlt(int size, int scale) {
	try {		
		DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locales.getCurrent());
		DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();
		StringBuffer result = new StringBuffer("alt='n"); // Negatives always allowed
		result.append(size > 9?"0":Integer.toString(size)); // Size		
		if (scale == 0 || !df.isGroupingUsed()) result.append("x"); // no grouping separator
		else {
			switch (symbols.getGroupingSeparator()) {					
				case ',':					
					result.append("c"); // comma
					break;
				case '.':					
					result.append("p"); // period
					break;
				case ' ':
					result.append("s"); // space
					break;
				case '\'': 
					result.append("a"); // apostrophe
					break;
				
				default:
					result.append("x"); // none					
			}
		}
		result.append(df.getGroupingSize());
		switch (symbols.getDecimalSeparator()) {		
			case ',':
				result.append("c"); // comma
				break;
			default:
				result.append("p"); // period		
		}
		result.append(scale > 9?"9":Integer.toString(scale)); // Scale
		result.append("'");
		return result.toString();
	}
	catch (Exception ex) {
		log.warn(XavaResources.getString("default_numeric_editor_configuration")); 
		return "alt='n0c3p2'";
	}
}
%>

    
    

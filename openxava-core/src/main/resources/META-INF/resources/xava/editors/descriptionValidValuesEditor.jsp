<%@page import="org.openxava.model.meta.MetaProperty"%>
<%
String propertyKey = request.getParameter("propertyKey");
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
Object description = null; 
int baseIndex = 1; 
int value = 0; 
Object ovalue = request.getAttribute(propertyKey + ".value");
MetaProperty validValuesProperty = (MetaProperty) request.getAttribute(propertyKey + ".validValuesProperty"); 
if (validValuesProperty == null) validValuesProperty = p;
if (validValuesProperty.hasValidValues()) {  	
	if (p.isNumber()) {
		value = ovalue==null?0:((Integer) ovalue).intValue();	
	}
	else {
		// We assume that if it isn't Number then it's an Enum of Java 5, we use instropection
		// to allow this code run in a Java 1.4 servlet container.
		baseIndex = 0;
		if (ovalue == null) {
			value = -1;	
		}
		else if (ovalue instanceof Number) { // Directly the ordinal
			value = ((Number) ovalue).intValue();
		}
		else { // An object of enum type
			value = ((Integer) org.openxava.util.XObjects.execute(ovalue, "ordinal")).intValue();
		}
	}
	int labelIndex = p != validValuesProperty && validValuesProperty.getMetaModel().isAnnotatedEJB3()?value - 1:value; 
	description = labelIndex == -1?"":validValuesProperty.getValidValueLabel(labelIndex);
}
%>
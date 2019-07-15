<jsp:useBean id="context" class="org.openxava.controller.ModuleContext" scope="session"/>
<jsp:useBean id="style" class="org.openxava.web.style.Style" scope="request"/>

<%@ page import="org.openxava.util.KeyAndDescription" %>
<%@ page import="org.openxava.util.Is" %>
<%@ page import="org.openxava.util.XavaResources" %>
<%@ page import="org.openxava.model.meta.MetaProperty" %>
<%@ page import="org.openxava.calculators.DescriptionsCalculator" %>
<%@ page import="org.openxava.formatters.IFormatter" %>
<%@ page import="org.openxava.filters.IFilter" %>
<%@ page import="org.openxava.filters.IRequestFilter" %>
<%@ page import="org.openxava.mapping.PropertyMapping"%>
<%@ page import="org.openxava.converters.IConverter"%>

<%
String viewObject = request.getParameter("viewObject");
viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject;
org.openxava.view.View view = (org.openxava.view.View) context.get(request, viewObject);
String propertyKey = request.getParameter("propertyKey");
String script = request.getParameter("script");
//modelForId is for have a different cache for each condition
String modelForId = "." + view.getModelName(); 
// conditionForId is for have a different cache for each condition
String conditionForId = request.getParameter("condition");
if (Is.emptyString(conditionForId)) conditionForId = request.getParameter("condicion");
conditionForId = Is.emptyString(conditionForId)?"":"." + conditionForId;
// orderByKeyForId is for have a different cache for each orderByKey
String orderByKeyForId = request.getParameter("orderByKey");
if (Is.emptyString(orderByKeyForId)) orderByKeyForId = request.getParameter("ordenadoPorClave");
orderByKeyForId = Is.emptyString(orderByKeyForId)?"":"." + orderByKeyForId;
//orderForId is for have a different cache for each order
String orderForId = request.getParameter("order");
if (Is.emptyString(orderForId)) orderForId = request.getParameter("orden");
orderForId = Is.emptyString(orderForId)?"":"." + orderForId;

String descriptionsCalculatorKey = propertyKey + modelForId + conditionForId + orderByKeyForId + orderForId + ".descriptionsCalculator"; 
DescriptionsCalculator calculator = (DescriptionsCalculator) request.getSession().getAttribute(descriptionsCalculatorKey);	

IFilter filter = null;
String filterClass=request.getParameter("filter");
if (Is.emptyString(filterClass)) filterClass=request.getParameter("filtro"); 
if (!Is.emptyString(filterClass)) {
	String filterKey = propertyKey + ".filter";
	filter = (IFilter) request.getSession().getAttribute(filterKey);
	if (filter == null) {
		try {
			filter = (IFilter) Class.forName(filterClass).newInstance();
			request.getSession().setAttribute(filterKey, filter);	
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.err.println(XavaResources.getString("descriptionsEditor_filter_warning", propertyKey));
		}
	}
	if (filter instanceof IRequestFilter) {
		((IRequestFilter) filter).setRequest(request);
	}
}


IFormatter formatter = null;
String descriptionsFormatterClass=request.getParameter("descriptionsFormatter");
if (descriptionsFormatterClass == null) {
	descriptionsFormatterClass=request.getParameter("formateadorDescripciones");
}
if (descriptionsFormatterClass != null) {
	String descriptionsFormatterKey = propertyKey + ".descriptionsFormatter";
	formatter = (IFormatter) request.getSession().getAttribute(descriptionsFormatterKey);	
	if (formatter == null) {
		try {
			formatter = (IFormatter) Class.forName(descriptionsFormatterClass).newInstance();
			request.getSession().setAttribute(descriptionsFormatterKey, formatter);	
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.err.println(XavaResources.getString("descriptionsEditor_descriptions_formatter_warning", propertyKey));
		}
	}
}

String parameterValuesProperties=request.getParameter("parameterValuesProperties");	
if (parameterValuesProperties == null) {
	parameterValuesProperties=request.getParameter("propiedadesValoresParametros");	
}
String parameterValuesStereotypes=request.getParameter("parameterValuesStereotypes");
if (parameterValuesStereotypes == null) {
	parameterValuesStereotypes=request.getParameter("estereotiposValoresParametros");
}
if (calculator == null) { 
	calculator = new DescriptionsCalculator();
	// The arguments in English and Spanish for compatibility with
	// a big amount of stereotypes made in spanish using this editor
	String condition = request.getParameter("condition");
	if (condition == null) condition = request.getParameter("condicion");
	calculator.setCondition(condition);
	String order = request.getParameter("order");
	if (order == null) order = request.getParameter("orden");
	calculator.setOrder(order);
	calculator.setUseConvertersInKeys(true);
	String model = request.getParameter("model");
	if (model == null) model = request.getParameter("modelo");
	calculator.setModel(model);
	String keyProperty = request.getParameter("keyProperty");
	if (keyProperty == null) keyProperty = request.getParameter("propiedadClave");
	calculator.setKeyProperty(keyProperty);
	String keyProperties = request.getParameter("keyProperties");
	if (keyProperties == null) keyProperties = request.getParameter("propiedadesClave");
	calculator.setKeyProperties(keyProperties);
	String descriptionProperty = request.getParameter("descriptionProperty");
	if (descriptionProperty == null) descriptionProperty = request.getParameter("propiedadDescripcion");
	calculator.setDescriptionProperty(descriptionProperty);
	String descriptionProperties = request.getParameter("descriptionProperties");
	if (descriptionProperties == null) descriptionProperties = request.getParameter("propiedadesDescripcion");
	calculator.setDescriptionProperties(descriptionProperties);
	String orderByKey = request.getParameter("orderByKey");
	if (orderByKey == null) orderByKey = request.getParameter("ordenadoPorClave");
	calculator.setOrderByKey(orderByKey);
	request.getSession().setAttribute(descriptionsCalculatorKey, calculator);
}
if (parameterValuesStereotypes != null || parameterValuesProperties != null) {	
	java.util.Iterator it = null;
	if (parameterValuesStereotypes != null) {
		it = view.getPropertiesNamesFromStereotypesList(parameterValuesStereotypes).iterator();		
	}
	else  {
		it = view.getPropertiesNamesFromPropertiesList(parameterValuesProperties).iterator();		
	}
	java.util.Collection p = new java.util.ArrayList();
	while (it.hasNext()) {
		String parameterValueKey = (String) it.next();		
		org.openxava.view.View v = null;
		if (parameterValueKey != null && parameterValueKey.startsWith("this.")) {
			parameterValueKey = parameterValueKey.substring(5);
			v = view;
		}
		else {
			v = view.getRoot();
		}
		Object parameterValue = parameterValueKey==null?null:v.getValue(parameterValueKey);
		
		if (parameterValueKey != null) { 
			PropertyMapping mapping = v.getMetaProperty(parameterValueKey).getMapping();
			if (mapping != null) {
				IConverter converter = mapping.getConverter();
				if (converter != null) {
					parameterValue = converter.toDB(parameterValue);
				}
			}
		}

		p.add(parameterValue);
	}
	calculator.setParameters(p, filter);
}
else if (filter != null) {
	calculator.setParameters(null, filter);
}
java.util.Collection descriptions = calculator.getDescriptions();
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String title = (p == null)?"":p.getDescription(request);
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
boolean editable = "true".equals(request.getParameter("editable"));
boolean label = org.openxava.util.XavaPreferences.getInstance().isReadOnlyAsLabel() || "true".equalsIgnoreCase(request.getParameter("readOnlyAsLabel"));
if (editable) {
		java.util.Iterator it = descriptions.iterator();
		String selectedDescription = "";
		String selectedKey = "";
		StringBuffer values = new StringBuffer("[");
		int maxDescriptionLength = 0;
		while (it.hasNext()) {
			KeyAndDescription cl = (KeyAndDescription) it.next();	
			String selected = "";
			String description = formatter==null?cl.getDescription().toString():formatter.format(request, cl.getDescription());
			if (description.length() > maxDescriptionLength) maxDescriptionLength = description.length();
			if (Is.equalAsStringIgnoreCase(fvalue, cl.getKey())) {
				selected = "selected"; 
				selectedDescription = description;
				selectedKey = cl.getKey().toString();
			} 		
			values.append("{label:\"");
			values.append(description.replaceAll("'", "&apos;").replaceAll("\"", "&Prime;")); 
			values.append("\",value:\"");
			values.append(cl.getKey().toString().replaceAll("'", "&apos;").replaceAll("\"", "&Prime;")); 
			values.append("\"}");
			if (it.hasNext()) values.append(",");
		} 
		values.append("]");
		String browser = request.getHeader("user-agent");
		maxDescriptionLength += 5;
		selectedDescription = selectedDescription.replaceAll("\"", "&quot;"); 
	%>
	<span class="<%=style.getDescriptionsList()%>">
	<%-- The JavaScript code depends on the order of the next elements --%>
	<input name="<%=propertyKey%>__CONTROL__" type="text" tabindex="1" class="xava_select <%=style.getEditor()%>" size="<%=maxDescriptionLength%>" <%=script%> title="<%=title%>" 
		data-values='<%=values%>' value="<%=selectedDescription%>"/>
	<input id="<%=propertyKey%>" type="hidden" name="<%=propertyKey%>" value="<%=selectedKey%>"/>
	<input type="hidden" name="<%=propertyKey%>__DESCRIPTION__" value="<%=selectedDescription%>"/>
	<a class="ox-layout-descriptions-editor-handler" href="javascript:descriptionsEditor.open('<%=propertyKey%>')"><i class="mdi mdi-menu-down"></i></a> 		
	<a class="ox-layout-descriptions-editor-handler" href="javascript:descriptionsEditor.close('<%=propertyKey%>')" style="display: none"><i class="mdi mdi-menu-up"></i></a>
	</span>
	<% 	
} else { 
	Object description = "";
	java.util.Iterator it = descriptions.iterator();
	while (it.hasNext()) {
		KeyAndDescription cl = (KeyAndDescription) it.next();
		if (Is.equalAsString(fvalue, cl.getKey())) {							
			description = formatter==null?cl.getDescription().toString():formatter.format(request, cl.getDescription());
			break;
		}
	}	
	if (label) {
%>

<%
Object b = (Object) request.getParameter("bold");
boolean bold = b == null ? false : new Boolean(b.toString()).booleanValue();
if (bold) { %> <b> <%}%> 

	<%=description%>&nbsp;
<% if (bold) { %> </b> <%} %>
<%
	}
	else {	
%>
	<input name="<%=propertyKey%>__DESCRIPTION__" class=<%=style.getEditor()%>
		type="text" 
		title="<%=title%>"
		maxlength="<%=description.toString().length()%>" 
		size="<%=description.toString().length() + 5%>" 
		value="<%=description%>"
		disabled
	/>
<%  } %>	
	<input type="hidden" name="<%=propertyKey%>" value="<%=fvalue%>"/>	
<% } %>			

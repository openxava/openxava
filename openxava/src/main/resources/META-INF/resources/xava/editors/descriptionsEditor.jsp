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
<%@ page import="java.util.Arrays"%>
<%@ page import="org.openxava.web.Ids"%>

<%
String viewObject = request.getParameter("viewObject");
viewObject = (viewObject == null || viewObject.equals(""))?"xava_view":viewObject;
org.openxava.view.View view = (org.openxava.view.View) context.get(request, viewObject);
String propertyKey = request.getParameter("propertyKey");
// modelForId is to have a different cache per model
String modelForId = "." + view.getModelName(); 
// conditionForId is to have a different cache per condition
String conditionForId = request.getParameter("condition");
if (Is.emptyString(conditionForId)) conditionForId = request.getParameter("condicion");
conditionForId = Is.emptyString(conditionForId)?"":"." + conditionForId;
// orderByKeyForId is to have a different cache per orderByKey
String orderByKeyForId = request.getParameter("orderByKey");
if (Is.emptyString(orderByKeyForId)) orderByKeyForId = request.getParameter("ordenadoPorClave");
orderByKeyForId = Is.emptyString(orderByKeyForId)?"":"." + orderByKeyForId;
// orderForId is to have a different cache per order
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

// Ensure configuration is present even if calculator existed already
// This mirrors the DWR behavior and guarantees proper key parsing on reload
{
    String condParam = request.getParameter("condition");
    if (condParam == null) condParam = request.getParameter("condicion");
    if (!Is.emptyString(condParam)) calculator.setCondition(condParam);

    String orderParam = request.getParameter("order");
    if (orderParam == null) orderParam = request.getParameter("orden");
    if (!Is.emptyString(orderParam)) calculator.setOrder(orderParam);

    calculator.setUseConvertersInKeys(true);

    String modelParam = request.getParameter("model");
    if (modelParam == null) modelParam = request.getParameter("modelo");
    if (!Is.emptyString(modelParam)) calculator.setModel(modelParam);

    String kpParam = request.getParameter("keyProperty");
    if (kpParam == null) kpParam = request.getParameter("propiedadClave");
    if (!Is.emptyString(kpParam)) calculator.setKeyProperty(kpParam);

    String kpsParam = request.getParameter("keyProperties");
    if (kpsParam == null) kpsParam = request.getParameter("propiedadesClave");
    if (!Is.emptyString(kpsParam)) calculator.setKeyProperties(kpsParam);

    String dpParam = request.getParameter("descriptionProperty");
    if (dpParam == null) dpParam = request.getParameter("propiedadDescripcion");
    if (!Is.emptyString(dpParam)) calculator.setDescriptionProperty(dpParam);

    String dpsParam = request.getParameter("descriptionProperties");
    if (dpsParam == null) dpsParam = request.getParameter("propiedadesDescripcion");
    if (!Is.emptyString(dpsParam)) calculator.setDescriptionProperties(dpsParam);

    String obkParam = request.getParameter("orderByKey");
    if (obkParam == null) obkParam = request.getParameter("ordenadoPorClave");
    if (!Is.emptyString(obkParam)) calculator.setOrderByKey(obkParam);
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
		else if (parameterValueKey != null && view.isMemberFromElementCollection(parameterValueKey)) {
			v = view;
		}		
		else {
			v = view.getRoot();
		}
		
		Object parameterValue = null; 
		if (parameterValueKey != null) {
			if (v.getMetaModel().containsMetaReference(parameterValueKey)) {
				parameterValue = v.getSubview(parameterValueKey).getEntity();
			}
			else {
				parameterValue = v.getValue(parameterValueKey);
				PropertyMapping mapping = v.getMetaProperty(parameterValueKey).getMapping();
				if (mapping != null) {
					IConverter converter = mapping.getConverter();
					if (converter != null) {
						parameterValue = converter.toDB(parameterValue);
					}
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
MetaProperty p = (MetaProperty) request.getAttribute(propertyKey);
String title = "";  
try {
    if (p == null) {
        String undecorated = Ids.undecorateRef(propertyKey);
        String[] split = undecorated.split("\\.");
        String[] noNull = Arrays.stream(split)
            .filter(value -> value != null && value.length() > 0)
            .toArray(size -> new String[size]);
        String refName = noNull[noNull.length - 2];
        String d = view.getMetaReference(refName).getDescription();
        title = (d == null) ? "" : d;
    } else {
        title = p.getDescription(request);
    }
} catch (Exception e) {
    title = "";
}
String fvalue = (String) request.getAttribute(propertyKey + ".fvalue");
// Always use on-demand loading for better performance

java.util.Collection descriptions = null;
String selectedDescription = "";
String selectedKey = "";

// Only load the selected item if there's a value
if (!Is.emptyString(fvalue)) {
	try {
		KeyAndDescription selected = calculator.findDescriptionByKey(fvalue);
		if (selected != null) {
			descriptions = java.util.Collections.singletonList(selected);
		} else {
			descriptions = java.util.Collections.emptyList();
		}
	} catch (Exception e) {
		descriptions = java.util.Collections.emptyList();
	}
} else {
	descriptions = java.util.Collections.emptyList();
}

boolean editable = "true".equals(request.getParameter("editable"));
boolean label = org.openxava.util.XavaPreferences.getInstance().isReadOnlyAsLabel() || "true".equalsIgnoreCase(request.getParameter("readOnlyAsLabel"));
if (editable) {		
		String descriptionValue = request.getParameter("descriptionValue");
		
		// Find selected item if we have descriptions loaded
		if (descriptions != null) {
			java.util.Iterator it = descriptions.iterator();
			while (it.hasNext()) {
				KeyAndDescription cl = (KeyAndDescription) it.next();
				String description = formatter==null?cl.getDescription().toString():formatter.format(request, cl.getDescription());
				if (descriptionValue != null && descriptionValue.equals(description) || Is.equalAsString(fvalue, cl.getKey())) {
					selectedDescription = description;
					selectedKey = cl.getKey().toString();
					break; // Found it, no need to continue
				}
			}
		} else if (!Is.emptyString(fvalue)) {
			// Find just the selected item by key
			KeyAndDescription selectedItem = calculator.findDescriptionByKey(fvalue);
			if (selectedItem != null) {
				String description = formatter==null?selectedItem.getDescription().toString():formatter.format(request, selectedItem.getDescription());
				selectedDescription = description;
				selectedKey = selectedItem.getKey().toString();
			}
		}
		
		// Calculate max description length for input sizing
		int maxDescriptionLength = Math.max((selectedDescription==null?0:selectedDescription.length()) + 5, 30);
		selectedDescription = selectedDescription.replaceAll("\"", "&quot;").replace("\\\\", "\\\\\\\\"); 
	%>
	<span class="<%=style.getDescriptionsList()%> <%=style.getEditor()%>">
	<%-- The JavaScript code depends on the order of the next elements --%>
    <input name="<%=propertyKey%>__CONTROL__" type="text" tabindex="1" class="xava_select <%=style.getEditor()%>" size="<%=maxDescriptionLength%>" title="<%=title%>" 
		data-view-object="<%=viewObject%>"
		data-limit="60"
		data-condition="<%=calculator.getCondition()%>"
		data-orderByKey="<%=request.getParameter("orderByKey")==null?"":request.getParameter("orderByKey")%>"
		data-order="<%=request.getParameter("order")==null?"":request.getParameter("order")%>"
		data-filter="<%=filterClass==null?"":filterClass%>"
		data-descriptionsFormatter="<%=descriptionsFormatterClass==null?"":descriptionsFormatterClass%>"
		data-parameterValuesProperties="<%=parameterValuesProperties==null?"":parameterValuesProperties%>"
		data-parameterValuesStereotypes="<%=parameterValuesStereotypes==null?"":parameterValuesStereotypes%>"
		data-model="<%=
			(request.getParameter("model")!=null?request.getParameter("model"):
			(request.getParameter("modelo")!=null?request.getParameter("modelo"):
			(calculator.getModel()==null?"":calculator.getModel())))
		%>"
		data-keyProperty="<%=request.getParameter("keyProperty")==null?"":request.getParameter("keyProperty")%>"
		data-keyProperties="<%=request.getParameter("keyProperties")==null?"":request.getParameter("keyProperties")%>"
		data-descriptionProperty="<%=request.getParameter("descriptionProperty")==null?"":request.getParameter("descriptionProperty")%>"
		data-descriptionProperties="<%=request.getParameter("descriptionProperties")==null?"":request.getParameter("descriptionProperties")%>"
		value="<%=selectedDescription%>"/>
	<input id="<%=propertyKey%>" type="hidden" name="<%=propertyKey%>" value="<%=selectedKey%>"/>
    <input type="hidden" name="<%=propertyKey%>__DESCRIPTION__" value="<%=selectedDescription%>"/>
	<a class="xava_descriptions_editor_open ox-layout-descriptions-editor-handler" data-property-key='<%=propertyKey%>'><i class="mdi mdi-menu-down"></i></a> 		
	<a class="xava_descriptions_editor_close ox-layout-descriptions-editor-handler ox-display-none" data-property-key='<%=propertyKey%>'><i class="mdi mdi-menu-up"></i></a>	
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

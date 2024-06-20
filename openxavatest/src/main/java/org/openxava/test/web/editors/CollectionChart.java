package org.openxava.test.web.editors;

import java.util.*;
import java.util.stream.*;

import org.apache.commons.lang3.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.view.*;

/**
 * tmr Poner algún comentario significativo
 * tmr Mover a OpenXava
 * 
 * @author Javier Paniza
 */

public class CollectionChart {
	
	private View view;
	private Collection<String> labelProperties;
	private Collection<String> dataProperties;
	
	public CollectionChart(View view) {
		this.view = view;
	}
	
	public Collection getLabels() { 
		return view.getCollectionValues().stream()
	        .map(item -> getLabelFor(item))  
	        .collect(Collectors.toList()); 
	}
	
	public Collection<Collection> getValues() { // tmr ¿Values o Data?
		Collection<Collection> result = new ArrayList<>();
		for (String property: getDataProperties()) { // tmr ¿Llamar a Strings.toCollection() es eficiente?
			result.add(getValuesFor(property));
		}
		return result;
	}
	
	private String getLabelFor(Map<String, Object> item) { // tmr
		StringBuffer result = new StringBuffer();
		for (String property: getLabelProperties()) { // tmr ¿Llamar a Strings.toCollection() es eficiente? 
			if (result.length() > 0) result.append(" "); 
			result.append(item.get(property));
		}
		return result.toString();
	}
	
	private Collection getValuesFor(String propertyName) { // tmr ¿Values o Data?
		Collection result = new ArrayList();
        result.add(view.getLabelFor(view.getMetaProperty(propertyName))); 
        result.addAll(view.getCollectionValues().stream()
            .map(item -> item.get(propertyName))
            .collect(Collectors.toList()));
        return result;
	}
	
	private Collection<String> createDefaultDataProperties() { 
		Collection<String> result = createDefaultProperties(5, property -> 
			property.isNumber() &&
			!StringUtils.containsAnyIgnoreCase(property.getName(), "year", "anyo", "anio", "number", "numero", "code", "codigo", "id"));
		if (result.isEmpty()) {
			result = createDefaultProperties(5, property -> property.isNumber());
		}
		return result;		
	}
		
	private Collection<String> createDefaultLabelProperties() { 
		Collection<String> result = createDefaultProperties(2, property -> 
			property.isNumber() &&
			StringUtils.containsAnyIgnoreCase(property.getName(), "year", "anyo", "anio", "number", "numero", "code", "codigo", "id"));
		if (result.size() == 2) return result; 
		result.addAll(createDefaultProperties(2 - result.size(), property -> 
			StringUtils.containsAnyIgnoreCase(property.getName(), XavaPreferences.getInstance().getDefaultDescriptionPropertiesValueForDescriptionsList()))); 
		if (result.size() == 2) return result;
		result.addAll(createDefaultProperties(2 - result.size(), property -> true));
		return result;
	}	
	
	private Collection<String> createDefaultProperties(int limit, java.util.function.Predicate<MetaProperty> filter) { 
		return view.getMetaPropertiesList().stream()
	        .filter(filter)
	        .map(MetaProperty::getName)
	        .limit(limit)
	        .collect(Collectors.toList());
	}	
	

	private Collection<String> getLabelProperties() {
		return labelProperties;
	}

	public void setLabelProperties(String labelProperties) {
		// tmr ¿Caché de esto? ME QUEDÉ POR AQUÍ. YA FUNCIONA. DECIDIR SI HACER CACHÉ
		this.labelProperties = Is.emptyString(labelProperties)?
			createDefaultLabelProperties():
			Strings.toCollection(labelProperties);
	}

	private Collection<String> getDataProperties() {
		return dataProperties;
	}

	public void setDataProperties(String dataProperties) {
		// tmr ¿Caché de esto?
		this.dataProperties = Is.emptyString(dataProperties)?
			createDefaultDataProperties():	
			Strings.toCollection(dataProperties);
	}	

}

package org.openxava.web.editors;

import java.util.*;
import java.util.stream.*;

import org.apache.commons.lang3.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;
import org.openxava.view.*;

/**
 * Helper class to be used from CollectionChart editor JSP code. 
 * 
 * @since 7.4
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
	
	public Collection<Collection> getData() { 
		Collection<Collection> result = new ArrayList<>();
		for (String property: getDataProperties()) { 
			result.add(getDataFor(property));
		}
		return result;
	}
	
	private String getLabelFor(Map<String, Object> item) { 
		StringBuffer result = new StringBuffer();
		for (String property: getLabelProperties()) {  
			if (result.length() > 0) result.append(" "); 
			result.append(item.get(property));
		}
		return result.toString();
	}
	
	private Collection getDataFor(String propertyName) { 
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
		Collection<String> descriptions = createDefaultProperties(1, property -> 
		StringUtils.containsAnyIgnoreCase(property.getName(), XavaPreferences.getInstance().getDefaultDescriptionPropertiesValueForDescriptionsList())); 
		result.addAll(descriptions); 
		if (!descriptions.isEmpty()) return result;
		result.addAll(createDefaultProperties(1, property -> true));
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
		this.labelProperties = Is.emptyString(labelProperties)?
			createDefaultLabelProperties():
			Strings.toCollection(labelProperties);
	}

	private Collection<String> getDataProperties() {
		return dataProperties;
	}

	public void setDataProperties(String dataProperties) {
		this.dataProperties = Is.emptyString(dataProperties)?
			createDefaultDataProperties():	
			Strings.toCollection(dataProperties);
	}	

}

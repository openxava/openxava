package org.openxava.web;

import java.util.Enumeration;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openxava.model.meta.MetaModel;
import org.openxava.model.meta.MetaProperty;
import org.openxava.model.meta.MetaReference;
import org.openxava.util.Messages;
import org.openxava.util.Strings;
import org.openxava.web.editors.WebEditors;

/**
 * Created on 21/08/2009
 * @author Ana Andres
 */
public class DescriptionsLists {
	
	public static final String COMPOSITE_KEY_SUFFIX = "__KEY__"; 
	
	public static void resetDescriptionsCache(HttpSession session) {
		Enumeration e = session.getAttributeNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			if (name.endsWith(".descriptionsCalculator")) {
				session.removeAttribute(name);
			}
		}		
	}
	
	/**
	 * Fills a map with reference values from a composite key string.
	 * 
	 * @since 7.6
	 * @param referenceValues Map to be filled with reference values
	 * @param ref MetaReference containing the reference definition
	 * @param value String value containing the composite key
	 * @param qualifier Qualifier for request attributes, can be null
	 * @param propertyPrefix Prefix for property names in the map, can be null
	 * @param request HttpServletRequest for setting attributes and parsing values
	 * @param errors Messages object for error handling during parsing
	 * @param viewName View name for editor formatting
	 * @param emptyIfNotBracketed If true and value doesn't start with '[', value is set to empty string.
	 *                           If false and value doesn't start with '[', value is wrapped with "[." and ".]".
	 */
	public static void fillReferenceValues(Map referenceValues, MetaReference ref, String value, 
			String qualifier, String propertyPrefix, HttpServletRequest request, 
			Messages errors, String viewName, boolean emptyIfNotBracketed) {
		
		MetaModel metaModel = ref.getMetaModelReferenced();
		if (!value.startsWith("[")) {
			value = emptyIfNotBracketed ? "" : "[." + value + ".]";
		}
		StringTokenizer st = new StringTokenizer(Strings.change(value, "..", ". ."), "[.]");
		
		for (String propertyName: metaModel.getAllKeyPropertiesNames()) {
			MetaProperty p = metaModel.getMetaProperty(propertyName);			 								
			Object propertyValue = null;
			
			if (st.hasMoreTokens()) { // if not then null is assumed. This is a case of empty value
				String stringPropertyValue = st.nextToken();
				propertyValue = WebEditors.parse(request, p, stringPropertyValue, errors, viewName);								
			}			
			
			if (WebEditors.mustToFormat(p, viewName)) {				
				if (qualifier != null) { 
					String valueKey = qualifier + "." + ref.getName() + "." + propertyName + ".value"; 
					request.setAttribute(valueKey, propertyValue);
				}
				referenceValues.put(propertyPrefix==null?propertyName:propertyPrefix + propertyName, propertyValue);
			}								
		}
	}
	
	/**
	 * Fills a map with reference values from a composite key string.
	 * This is a convenience method that uses the default behavior for Tab.
	 * 
	 * @param referenceValues Map to be filled with reference values
	 * @param ref MetaReference containing the reference definition
	 * @param value String value containing the composite key
	 * @param qualifier Qualifier for request attributes, can be null
	 * @param propertyPrefix Prefix for property names in the map, can be null
	 * @param request HttpServletRequest for setting attributes and parsing values
	 * @param errors Messages object for error handling during parsing
	 * @param viewName View name for editor formatting
	 */
	public static void fillReferenceValues(Map referenceValues, MetaReference ref, String value, 
			String qualifier, String propertyPrefix, HttpServletRequest request, 
			Messages errors, String viewName) {
		
		// By default, use the Tab behavior (wrap with brackets)
		fillReferenceValues(referenceValues, ref, value, qualifier, propertyPrefix, 
			request, errors, viewName, false);
	}
}

package org.openxava.web;

import java.util.*;

import javax.servlet.http.*;

import org.openxava.model.meta.*;
import org.openxava.util.*;

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
		Map<String, Object> parsed = parseKeyValues(metaModel, value, request, errors, viewName, emptyIfNotBracketed);
		for (String propertyName: metaModel.getAllKeyPropertiesNames()) {
			MetaProperty p = metaModel.getMetaProperty(propertyName);
			Object propertyValue = parsed.get(propertyName);
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

	/**
	 * Parses a key string using MetaModel key properties. <p>
	 * 
	 * It can be a composite key like "[.1.4.]" or a simple key like "1".
	 * Values are parsed via WebEditors.parse() to ensure consistency with editors.
	 *
	 * @since 7.6
	 */
	public static Map<String, Object> parseKeyValues(MetaModel metaModel, String value, 
			HttpServletRequest request, Messages errors, String viewName, boolean emptyIfNotBracketed) {
		Map<String, Object> result = new HashMap<String, Object>();
		if (value == null) return result;
		if (!value.startsWith("[")) {
			value = emptyIfNotBracketed ? "" : "[." + value + ".]";
		}
		StringTokenizer st = new StringTokenizer(Strings.change(value, "..", ". ."), "[.]");
		for (String propertyName: metaModel.getAllKeyPropertiesNames()) {
			MetaProperty p = metaModel.getMetaProperty(propertyName);
			Object propertyValue = null;
			if (st.hasMoreTokens()) {
				String stringPropertyValue = st.nextToken();
				// If the token is the literal string "null" treat it as Java null
				if ("null".equals(stringPropertyValue)) {
					propertyValue = null;
				}
				else {
					propertyValue = WebEditors.parse(request, p, stringPropertyValue, errors, viewName);
				}
			}
			result.put(propertyName, propertyValue);
		}
		return result;
	}

	/**
	 * Convenience overload to parse composite key values without providing request/view. <p>
	 * 
	 * It can be a composite key like "[.1.4.]" or a simple key like "1".
	 * Uses default behavior for Tab (wrap with brackets if needed).
	 *
	 * @since 7.6
	 */
	public static Map<String, Object> parseKeyValues(MetaModel metaModel, String value) {
		return parseKeyValues(metaModel, value, null, new Messages(), null, false);
	}

	/**
	 * Overload to fill a provided map with parsed key values using only MetaModel.
	 * Delegates to parseCompositeKeyValues to avoid duplicating parsing logic.
	 *
	 * @since 7.6
	 */
	public static void fillReferenceValues(Map<String, Object> values, MetaModel metaModel, String value) {
		values.putAll(parseKeyValues(metaModel, value));
	}
}

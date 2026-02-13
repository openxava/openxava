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
		// Delegate to the generic overload using the MetaModel key properties
		return parseKeyValues(metaModel, metaModel.getAllKeyPropertiesNames(), value, request, errors, viewName, emptyIfNotBracketed);
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
     * Parses a key string using the given key property names instead of the MetaModel primary key.
     * Reuses the same parsing logic to keep behavior consistent.
     *
     * @since 7.6
     */
    public static Map<String, Object> parseKeyValues(MetaModel metaModel, Collection<String> keyPropertiesNames, String value,
            HttpServletRequest request, Messages errors, String viewName, boolean emptyIfNotBracketed) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (value == null) return result;
        if (!value.startsWith("[")) {
            value = emptyIfNotBracketed ? "" : "[." + value + ".]";
        }
        StringTokenizer st = new StringTokenizer(Strings.change(value, "..", ". ."), "[.]");
        for (String propertyName: keyPropertiesNames) {
            MetaProperty p = metaModel.getMetaProperty(propertyName);
            Object propertyValue = null;
            if (st.hasMoreTokens()) {
                String stringPropertyValue = st.nextToken();
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
     * Convenience overload to parse using provided key property names without request/view.
     *
     * @since 7.6
     */
    public static Map<String, Object> parseKeyValues(MetaModel metaModel, Collection<String> keyPropertiesNames, String value) {
        return parseKeyValues(metaModel, keyPropertiesNames, value, null, new Messages(), null, false);
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
	
	/**
	 * Formats key values into the standard key string representation. <p>
	 * 
	 * For a single key property, returns the raw value as a string (e.g. "2").
	 * For multiple key properties, returns the composite format "[.v1.v2.]" (e.g. "[.1.4.]").
	 * The key properties are obtained from the MetaModel, so nested keys are supported.
	 *
	 * @param metaModel  MetaModel to obtain key property names from
	 * @param keyValues  Map from key property name to its value
	 * @return The formatted key string
	 * @since 7.7
	 */
	public static String toKeyString(MetaModel metaModel, Map<String, Object> keyValues) {
		Collection<String> keyPropertiesNames = metaModel.getAllKeyPropertiesNames();
		if (keyPropertiesNames.size() == 1) {
			Object v = keyValues.get(keyPropertiesNames.iterator().next());
			return v == null ? "" : v.toString();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("[.");
		boolean first = true;
		for (String keyName : keyPropertiesNames) {
			if (!first) sb.append(".");
			Object v = keyValues.get(keyName);
			sb.append(v == null ? "" : v.toString());
			first = false;
		}
		sb.append(".]");
		return sb.toString();
	}
}

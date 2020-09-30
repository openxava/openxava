package org.openxava.util;

import java.util.*;



import org.apache.commons.logging.*;
import org.openxava.application.meta.*;

/**
 * Utility class for obtain the i18n of the labels. <p>
 * 
 * @author Javier Paniza
 */

public class Labels {

	private static Log log = LogFactory.getLog(Labels.class);
	private static Map<String, String> labels = Collections.synchronizedMap(new HashMap<>()); 
	private static Map<String, String> customLabels; 
	
	/**
	 * On any error returns the sent <code>id</code> with the first letter in uppercase.
	 */
	public static String getQualified(String id) {
		return getQualified(id, Locales.getCurrent()); 
	}	
	

	/**
	 * On any error returns the sent <code>id</code> with the first letter in uppercase.
	 */
	public static String getQualified(String id, Locale locale) {
		return get(id, locale, null, true); 
	}
	
	public static String get(String id) {
		return get(id, Locales.getCurrent());
	}

	
	/**
	 * On any error returns the sent <code>id</code> with the first letter in uppercase.
	 */
	public static String get(String id, Locale locale) {
		return get(id, locale, null, false); 
	}
	
	/**
	 * Add or change a label by locale. <p>
	 * 
	 * The result is not persistent, after reinit the application the changes are gone.
	 * 
	 * @since 5.8
	 */
	public static void put(String id, Locale locale, String label) { 
		String key = toKey(id, locale, false); 
		if (customLabels == null) customLabels = new HashMap<>();
		customLabels.put(key, label);
		labels = customLabels;
	}
	
	private static String toKey(String id, Locale locale, boolean qualified) { 
		return id + "::" + locale.getLanguage() + "::" + qualified; 
	}
	
	/** 
	 * Qualified label. <p>
	 * 
	 * If you sent customer.warehouse.name it return "Name of Warehouse of Customer". <br>
	 * 
	 * If <code>id</code> is not found, or other error returns <code>defaultValue</code>
	 */
	public static String getQualified(String id, Locale locale, String defaultValue) {
		return get(id, locale, defaultValue, true);
	}
	
	
	/**
	 * If <code>id</code> is not found, or other error returns <code>defaultValue</code>
	 */
	public static String get(String id, Locale locale, String defaultValue) {
		return get(id, locale, defaultValue, false);
	}
		
	/**
	 * If there is not label registered for the id returns the defaultValue, if defaultValue is null then generates a label
	 * from the id. 
	 */
	private static String get(String id, Locale locale, String defaultValue, boolean qualified) { 
		String label = get(id, locale, qualified);
		if (label == null) {
			label = defaultValue==null?Strings.javaIdentifierToNaturalLabel(Strings.lastToken(id, ".")):defaultValue; 
			String key = toKey(id, locale, qualified);
			labels.put(key, label);
		}
		return label;
	}
		
	/**
	 * If there is not label return null.
	 */
	private static String get(String id, Locale locale, boolean qualified) { 
		String key = toKey(id, locale, qualified); 
		String label = labels.get(key);
		if (label == null) {
			label = getWithoutCache(id, locale, qualified);
			if (label == null) {
				label = null; 
			}
			else {
				labels.put(key, label);
			}
		}
		return label;
	}	

	private static String getWithoutCache(String id, Locale locale, boolean qualified) { 
		try {
			return getImpl(id, locale, qualified);
		}
		catch (Exception ex) {
			if (XavaPreferences.getInstance().isI18nWarnings()) {
				log.warn(XavaResources.getString("element_i18n_warning", id));
			} 
			return null;
		}		
	}		
		
	private static String getImpl(String id, Locale locale, boolean qualified) throws MissingResourceException, XavaException {
		if (id == null) return "";
		try {			
			return getResource(id, locale);
		}
		catch (MissingResourceException ex) {		
			int idxDot = id.indexOf(".");
			if (idxDot < 0) throw ex;			
			String idWithoutQualifier = removeViewOrTab(id);
			if (idWithoutQualifier != null) {
				return get(idWithoutQualifier, locale, false); 
			}
			String parent = id.substring(0, idxDot);
			if (!qualified || idxDot > 0 && Character.isUpperCase(id.charAt(0))) {
				return get(id.substring(idxDot + 1), locale, qualified);
			}
			else {
				String composeLabel = get(id.substring(idxDot + 1), locale, null, qualified) + " " + 
					XavaResources.getString("of", locale) + " " +
					get(parent, locale, null, false);
				return Strings.firstUpper(composeLabel.toLowerCase());
			}			
		}
	} 
		
	/**
	 * @return null if not contains view or tab
	 */
	private static String removeViewOrTab(String id) {
		String r = removeQualifier(id, ".view.", ".views.");
		if (r != null) return r;
		return removeQualifier(id, ".tab.properties.", ".tabs.");		
	}
	
	private static String removeQualifier(String id, String singular, String plural)
	{
		try {
			int idx = id.indexOf(singular);
			if (idx >= 0) {
				return id.substring(0, idx) + id.substring(idx + singular.length() - 1);
			}
			idx = id.indexOf(plural);
			if (idx >= 0) {
				StringTokenizer st = new StringTokenizer(id.substring(idx + plural.length()), ".");
				if (st.countTokens() == 1) { // es la propia vista, no un miembro
					return id.substring(0, idx) + id.substring(idx + plural.length() - 1);
				}
				else { // it is a member
					String viewName = st.hasMoreTokens()?st.nextToken():"";
					int plus = 0;
					if (".tabs.".equals(plural)) {
						if (id.indexOf("properties") >= 0) plus="properties".length();
						if (id.indexOf("title") >= 0) plus= "title".length();					
					}								
					return id.substring(0, idx) + id.substring(idx + plural.length() + viewName.length() + plus);
				}
			}
			return null;
		}
		catch (Exception ex) {
			if (XavaPreferences.getInstance().isI18nWarnings()) {
				log.warn(XavaResources.getString("label_i18n_warning", id));
			}
			return null;
		}
	}	
	
	public static boolean exists(String id) {				
		return exists(id, Locales.getCurrent());
	}
	
	public static boolean existsExact(String id) throws XavaException { 
		return existsExact(id, Locale.getDefault());
	}
	
	public static boolean exists(String id, Locale locale) {
		if (id == null) return false;
		try {
			getResource(id, locale);
			return true;
		}
		catch (MissingResourceException ex) {
			String idWithoutQualifier = removeViewOrTab(id);
			if (idWithoutQualifier != null) return exists(idWithoutQualifier, locale);
			int idx = id.indexOf(".");
			if (idx < 0) return false;
			return exists(id.substring(idx + 1));
		}
		catch (Exception ex) {
			log.warn(ex.getMessage());
			return false;
		}
						
	}
	
	public static boolean existsExact(String id, Locale locale) throws XavaException {
		if (id == null) return false;
		if (labels.containsKey(id)) return true; 
		try {
			getResource(id, locale);
			return true;
		}
		catch (MissingResourceException ex) {
			return false;
		}
	}
		
	private static String getResource(String id, Locale locale) throws MissingResourceException, XavaException {
		try { 
			return getExactResource(id, locale);
		}
		catch (MissingResourceException ex) {
			try {
				return getExactResource(Strings.firstUpper(id), locale);
			}
			catch (MissingResourceException ex2) {
				try {
					return getExactResource(Strings.firstLower(id), locale);
				}
				catch (MissingResourceException ex3) {
					try {
						return getExactResource(id.toUpperCase(), locale);
					}
					catch (MissingResourceException ex4) {
						return getExactResource(id.toLowerCase(), locale);
					}
				}
			}
		}
	}	
	
	private static String getExactResource(String id, Locale locale) throws MissingResourceException, XavaException {
		String name = "UNKNOW";
		try {
			Iterator it = MetaApplications.getApplicationsNames().iterator();
			while (it.hasNext()) {
				name = (String) it.next();
				try {
					ResourceBundle rb = ResourceBundle.getBundle(name + "-labels", locale);
					return rb.getString(id);
				}
				catch (MissingResourceException ex) {
				}						
				try {
					ResourceBundle rb = ResourceBundle.getBundle("Etiquetas" + name, locale);
					return rb.getString(id);
				}
				catch (MissingResourceException ex) {
				}			
			}		
			ResourceBundle rb = ResourceBundle.getBundle("Labels", locale);
			return rb.getString(id);
		}
		catch (MissingResourceException ex) {
			throw ex;
		}
		catch (Exception ex) {
			log.warn("Error translating " + id + ". We assume that the resource is missing");
			throw new MissingResourceException("Cannot obtain resource, cause: " + ex.getLocalizedMessage(), name, id);
		}
	}
	
	public static String removeUnderlined(String label) {				
		int idx =  label.indexOf('_');
		if (idx < 0) return label;
		String ini = idx > 0?label.substring(0, idx - 1):"";
		String end = idx == label.length() - 1?"":label.substring(idx + 1);
		return ini + end;
	}
		
}

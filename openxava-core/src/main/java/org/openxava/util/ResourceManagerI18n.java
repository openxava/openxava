package org.openxava.util;

import java.text.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.application.meta.*;

/**
 * @author Javier Paniza
 */

public class ResourceManagerI18n {
	
	private static Log log = LogFactory.getLog(ResourceManagerI18n.class);
	
	private String resourcesFile;
	private String englishPrefix;
	private String spanishPrefix;
	
	public ResourceManagerI18n(String resourcesFile) {
		Assert.assertNotNull("Resource file is required", resourcesFile); // this message cannot be i18n
		this.resourcesFile = resourcesFile;
	}
	
	public ResourceManagerI18n(String resourcesFile, String englishPrefix, String spanishPrefix) {
		Assert.assertNotNull("Resource file is required", resourcesFile); // this message cannot be i18n
		this.resourcesFile = resourcesFile;
		this.englishPrefix = englishPrefix;
		this.spanishPrefix = spanishPrefix;
	}
	
	
	public String getString(String key) {
		return getString(Locales.getCurrent(), key);
	}
	
	public String getString(String key, Object argv0) {
		return getString(key, new Object [] { argv0 });
	}
	
	public String getString(String key, Object argv0, Object argv1) {
		return getString(key, new Object [] { argv0, argv1 });
	}
	
	public String getString(String key, Object argv0, Object argv1, Object argv2) {
		return getString(key, new Object [] { argv0, argv1, argv2 });
	}
	
	public String getString(String key, Object argv0, Object argv1, Object argv2, Object argv3) {
		return getString(key, new Object [] { argv0, argv1, argv2, argv3 });
	}
	
	public String getString(String key, Object argv0, Object argv1, Object argv2, Object argv3, Object argv4) { 
		return getString(key, new Object [] { argv0, argv1, argv2, argv3, argv4 });
	}	
		
	public String getString(String key, Object [] argv) {
		MessageFormat formatter = new MessageFormat("");
		formatter.setLocale(Locales.getCurrent());
		formatter.applyPattern(getString(key));
		return formatter.format(argv);		
	}
	
	public String getString(Locale locale, String key) {
		try {
			Iterator it = MetaApplications.getApplicationsNames().iterator();
			while (it.hasNext()) {
				String name = (String) it.next();
				if (englishPrefix != null) {
					try {
						ResourceBundle rb = ResourceBundle.getBundle(name + englishPrefix, locale);
						return rb.getString(key);
					}
					catch (MissingResourceException ex) {
					}							
				}
				if (spanishPrefix != null) {
					try {
						ResourceBundle rb = ResourceBundle.getBundle(spanishPrefix + name, locale);
						return rb.getString(key);
					}
					catch (MissingResourceException ex) {
					}			
				}
			}	
		}
		catch (Exception ex) {
			log.error("Resource " + key + " cannot be translated using application specific resources. We use only " + resourcesFile,ex);
		}
		try {
			ResourceBundle rb = ResourceBundle.getBundle(resourcesFile, locale);
			return rb.getString(key);
		}
		catch (MissingResourceException ex) {
			if (XavaPreferences.getInstance().isI18nWarnings()) {
				log.warn("Impossible to translate element with id " + key);
			}
			return key;
		}
	}
	
	public String getString(Locale locale, String key, Object argv0) {		
		return getString(locale, key, new Object [] { argv0 });
	}
	
	public String getString(Locale locale, String key, Object argv0, Object argv1) {		
		return getString(locale, key, new Object [] { argv0, argv1 });
	}

	public String getString(Locale locale, String key, Object argv0, Object argv1, Object argv2) {		
		return getString(locale, key, new Object [] { argv0, argv1, argv2 });
	}
	
	public String getString(Locale locale, String key, Object [] argv) {		
		MessageFormat formateador = new MessageFormat("");
		formateador.setLocale(locale);
		formateador.applyPattern(getString(locale, key));
		return formateador.format(argv);		
	}
	

	public int getChar(String key) {
		try {
			String s = ResourceBundle.getBundle(resourcesFile).getString(key);
			if (s == null) return ' ';
			if (s.length() == 0) return ' ';
			return s.charAt(0);			
		}
		catch (MissingResourceException ex) {
			if (XavaPreferences.getInstance().isI18nWarnings()) {
				log.warn("WARNING! Translation for character " + key + " not found. Blank is assumed");
			}
			return ' ';
		}
	}	
	
}

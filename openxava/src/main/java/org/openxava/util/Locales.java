package org.openxava.util;

import java.util.*;

import javax.servlet.http.HttpServletRequest;




/**
 * Access to the locale associated to the current thread. <p> 
 * 
 * @author Javier Paniza
 */

public class Locales {
	
	final private static ThreadLocal current = new ThreadLocal();
	// Cached allowed languages parsed from XavaPreferences.availableLanguages
	private static java.util.List<String> cachedAllowedLanguages;
	private static boolean cachedAllowedLanguagesInitialized = false;
	
	/**
	 * The Locale associated to the current thread. <p>
	 * 
	 * @return Never null. If no locale associated, return default Locale.
	 */
	public static Locale getCurrent() {
		Locale r = (Locale) current.get();
		if (r == null) return Locale.getDefault();
		return r;
	}
	
	/**
	 * Associated a Locale to the current thread. <p>
	 */	
	public static void setCurrent(Locale locale) {
		current.set(locale);
	}
	
	/**
	 * Associated the Locale of the request to the current thread. <p> 
	 */
	public static void setCurrent(HttpServletRequest request) {
		Locale sessionLocale = (Locale) request.getSession().getAttribute("xava.portal.locale");
		java.util.List<String> allowed = getAllowedLanguagesCached();
		if (allowed == null) { // No restriction defined (or malformed)
			if (sessionLocale == null) current.set(request.getLocale());
			else current.set(sessionLocale);
			return;
		}
		// 1) Prefer session locale if allowed
		if (sessionLocale != null) {
			String lang = sessionLocale.getLanguage();
			if (lang != null && allowed.contains(lang.toLowerCase(java.util.Locale.ROOT))) {
				current.set(new Locale(lang)); // ignore country
				return;
			}
		}
		// 2) Try browser preferred locales in order
		for (java.util.Enumeration<java.util.Locale> e = request.getLocales(); e.hasMoreElements();) {
			java.util.Locale l = e.nextElement();
			String lang = l.getLanguage();
			if (lang != null && allowed.contains(lang.toLowerCase(java.util.Locale.ROOT))) {
				current.set(new Locale(lang)); // ignore country
				return;
			}
		}
		// 3) Fallback: first allowed language
		current.set(new Locale(allowed.get(0)));
	}

	/**
	 * Returns cached allowed languages list, or null if unrestricted (empty/missing config).
	 */
	private static java.util.List<String> getAllowedLanguagesCached() {
		if (cachedAllowedLanguagesInitialized) return cachedAllowedLanguages;
		synchronized (Locales.class) {
			if (cachedAllowedLanguagesInitialized) return cachedAllowedLanguages;
			String available = XavaPreferences.getInstance().getAvailableLanguages();
			if (available == null || available.trim().isEmpty()) {
				cachedAllowedLanguages = null; // unrestricted
				cachedAllowedLanguagesInitialized = true;
				return cachedAllowedLanguages;
			}
			String[] parts = available.split(",");
			java.util.List<String> allowed = new java.util.ArrayList<>();
			for (String p : parts) {
				if (p == null) continue;
				String s = p.trim();
				if (s.isEmpty()) continue;
				s = s.replace('_', '-');
				int dash = s.indexOf('-');
				if (dash > 0) s = s.substring(0, dash);
				s = s.toLowerCase(java.util.Locale.ROOT);
				if (!allowed.contains(s)) allowed.add(s);
			}
			cachedAllowedLanguages = allowed.isEmpty() ? null : allowed; // null => unrestricted
			cachedAllowedLanguagesInitialized = true;
			return cachedAllowedLanguages;
		}
	}

} 

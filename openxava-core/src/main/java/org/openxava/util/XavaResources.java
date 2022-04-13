package org.openxava.util;

import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;

/**
 * To obtain i18n values from OpenXava resource files. <p>
 * 
 * Search resources in the next files and order:
 * <ul>
 * <li><i>[MyProject]-message.properties</i> in the folder <i>xava</i> of your project.</li>
 * <li><i>Mensajes[MiProyecto].properties</i> in the folder <i>xava</i> of your project</li>
 * <li><i>Messages.properties</i> in <i>OpenXava/xava</i></li>
 * </ul>
 * [MyProject] is the concrete name of your project.<br>
 * 
 * In this way your i18n translations have preferences over the standard ones of OpenXava.<br> 
 *   
 * @author Javier Paniza
 * @see Labels
 * @see Messages
 */

public class XavaResources {
	
	private static final ResourceManagerI18n impl = new ResourceManagerI18n("Messages", "-messages", "Mensajes"); 
	private static Log log = LogFactory.getLog(XavaResources.class);
		
	public static String getString(String key) {	
		return impl.getString(key); 
	}
	
	public static String getString(String key, Object argv0) {
		return impl.getString(key, argv0); 
	}
	
	public static String getString(String key, Object argv0, Object argv1) {
		return impl.getString(key, argv0, argv1); 
	}
	
	public static String getString(String key, Object argv0, Object argv1, Object argv2) {
		return impl.getString(key, argv0, argv1, argv2);
	}
	
	public static String getString(String key, Object argv0, Object argv1, Object argv2, Object argv3) {
		return impl.getString(key, argv0, argv1, argv2, argv3);
	}
	
	public static String getString(String key, Object argv0, Object argv1, Object argv2, Object argv3, Object argv4) { 
		return impl.getString(key, argv0, argv1, argv2, argv3, argv4);
	}	

			
	public static String getString(String key, Object [] argv) {
		return impl.getString(key, argv);
	}

	public static String getString(Locale locale, String key) {	
		return impl.getString(locale, key);
	}
	
	public static String getString(Locale locale, String key, Object argv0) {	
		return impl.getString(locale, key, argv0);
	}
	
	public static String getString(Locale locale, String key, Object argv0, Object argv1) {	
		return impl.getString(locale, key, argv0, argv1);
	}
	
	public static String getString(Locale locale, String key, Object argv0, Object argv1, Object argv2) {	
		return impl.getString(locale, key, argv0, argv1, argv2);
	}

	public static String getString(HttpServletRequest request, String key) {	
		return impl.getString(getLocale(request), key);
	}
	
	public static String getString(HttpServletRequest request, String key, Object argv1) {
		return impl.getString(getLocale(request), key, argv1);
	}
	
	public static String getString(HttpServletRequest request, String key, Object argv1, Object argv2) {
		return impl.getString(getLocale(request), key, argv1, argv2);
	}
	
	public static String getString(HttpServletRequest request, String key, Object argv1, Object argv2, Object argv3) { 
		return impl.getString(getLocale(request), key, argv1, argv2, argv3);
	}	

	
	public static String getString(HttpServletRequest request, String key, Object [] argv) {
		return impl.getString(getLocale(request), key, argv);
	}
	

	public static int getChar(String key) {
		return impl.getChar(key);
	}	
	
	/**
	 * Locale used to obtain resource in web application. <p>
	 */
	public static Locale getLocale(ServletRequest request) { 
		Locale result = Locales.getCurrent();
		if (result == null) return request==null?Locale.getDefault():request.getLocale();
		return result;
	}
	
}

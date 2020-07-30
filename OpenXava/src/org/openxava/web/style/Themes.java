package org.openxava.web.style;

import java.util.*;
import java.util.prefs.*;

import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.application.meta.*;
import org.openxava.util.*;


/**
 *  
 * @since 6.3
 * @author Javier Paniza
 */

public class Themes {
	
	private static Log log = LogFactory.getLog(Themes.class); 
	private static IThemeProvider provider; 
	
	/** @since 6.4 */
	public static void setProvider(IThemeProvider theProvider) { 
		provider = theProvider;
	}
	
	public static boolean isChooserEnabled(HttpServletRequest request) { 
		if (Users.getCurrent() == null) return false; 
		if (provider != null && provider.getCSS(request) != null) return false;
		return !Is.emptyString(XavaPreferences.getInstance().getThemes());
	}
	
	public static String getCSS(HttpServletRequest request) {
		if (provider != null) {
			String providedTheme = provider.getCSS(request);
			if (providedTheme != null) return providedTheme;
		}
		if (!isChooserEnabled(request)) { 
			return XavaPreferences.getInstance().getStyleCSS(); 
		}
		String newTheme = request.getParameter("theme");
		try {
			if (!Is.emptyString(newTheme)) {
				Preferences pref = getPreferences();
				pref.put("theme", newTheme);
				pref.flush();
				return newTheme;
			}		
			return getPreferences().get("theme", XavaPreferences.getInstance().getStyleCSS());
		} 
		catch (Exception ex) {
			log.warn(XavaResources.getString("user_theme_error_using_default"), ex);
			return XavaPreferences.getInstance().getStyleCSS();
		}
	}
	
	public static Collection<String> getAll() {
		String themes = XavaPreferences.getInstance().getThemes();
		return Strings.toCollection(themes, ",");
	}
	
	/** @since 6.4 */
	public static String cssToLabel(String cssFile) { 
		return Strings.firstUpper(cssFile.replace(".css", "").replace("-", " "));
	}
	
	private static Preferences getPreferences() throws BackingStoreException { 
		return Users.getCurrentPreferences().node("theme." + MetaApplications.getMainMetaApplication().getName());
	}
	
}

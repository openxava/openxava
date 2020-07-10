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
	private static IThemeProvider provider; // tmp
	
	public static void setProvider(IThemeProvider theProvider) { // tmp
		provider = theProvider;
	}
	
	// tmp public static boolean isChooserEnabled() {
	public static boolean isChooserEnabled(HttpServletRequest request) { // tmp
		// tmp ini
		if (provider != null && provider.getCSS(request) != null) return false;
		// tmp fin
		return !Is.emptyString(XavaPreferences.getInstance().getThemes());
	}
	
	public static String getCSS(HttpServletRequest request) {
		// tmp ini
		if (provider != null) {
			String providedTheme = provider.getCSS(request);
			if (providedTheme != null) return providedTheme;
		}
		// tmp fin
		if (!isChooserEnabled(request)) { // tmp request
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
	
	public static String cssToLabel(String cssFile) { // tmp
		return Strings.firstUpper(cssFile.replace(".css", "").replace("-", " "));
	}
	
	public static String labelToCSS(String label) { // tmp
		return Strings.firstLower(label.replace(" ", "-")) + ".css";
	}

	
	private static Preferences getPreferences() throws BackingStoreException { 
		return Users.getCurrentPreferences().node("theme." + MetaApplications.getMainMetaApplication().getName());
	}
	

}

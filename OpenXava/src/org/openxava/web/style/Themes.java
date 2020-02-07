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
	
	public static boolean isChooserEnabled() {
		return !Is.emptyString(XavaPreferences.getInstance().getThemes());
	}
	
	public static String getCSS(HttpServletRequest request) { 
		if (!isChooserEnabled()) {
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
	
	private static Preferences getPreferences() throws BackingStoreException { 
		return Users.getCurrentPreferences().node("theme." + MetaApplications.getMainMetaApplication().getName());
	}
	

}

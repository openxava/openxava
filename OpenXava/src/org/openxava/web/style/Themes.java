package org.openxava.web.style;

import java.util.*;
import java.util.prefs.*;

import javax.servlet.http.*;

import org.openxava.application.meta.*;
import org.openxava.util.*;

/**
 * tmp 
 * 
 * @author Javier Paniza
 */

public class Themes {
	
	public static boolean isChooserEnabled() {
		return !Is.emptyString(XavaPreferences.getInstance().getThemes());
	}
	
	public static String getCSS(HttpServletRequest request) { 
		if (!isChooserEnabled()) {
			System.out.println("[Themes.getCSS] Chooser not enabled"); // tmp
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
		catch (Exception e) {
			e.printStackTrace(); // tmp log
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

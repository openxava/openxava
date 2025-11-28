package org.openxava.web;

import java.net.*;
import java.util.*;
import java.util.Collections;
import java.util.stream.*;

import javax.servlet.*;

import org.openxava.util.*;

/**
 * Utilities used from JSP files for user help. 
 * 
 * @since 7.6.3
 * @author Javier Paniza 
 */
public class Helps {
	
    public static String urlForModule(ServletContext sc, String application, String module) {
        String language = Locales.getCurrent().getLanguage();  
        String prefix = XavaPreferences.getInstance().getHelpPrefix();
        String suffix = XavaPreferences.getInstance().getHelpSuffix();

        if (prefix.startsWith("http:") || prefix.startsWith("https:")) {
            String href = prefix;
            if (href.endsWith("_")) href = href + language;
            if (!Is.emptyString(suffix)) href = href + suffix;
            return href;
        }

        String baseDir = "/" + prefix; 
        String baseName = module;                          

        String direct = baseDir + baseName + "_" + language + suffix;
        
        if (getResource(sc, direct) != null) {
            return "/" + application + direct;
        }

        Set<String> files = sc.getResourcePaths(baseDir);
        if (files != null) {
            List<String> variants = files.stream()
                .filter(f -> f.startsWith(baseDir + baseName + "_"))
                .collect(Collectors.toList());

            if (!variants.isEmpty()) {
                // Elegir la primera variante disponible: genérico y sin preferencias culturales
                Collections.sort(variants);
                return "/" + application + variants.get(0);
            }
        }

        String noLang = baseDir + baseName + suffix;
        if (getResource(sc, noLang) != null) {
            return "/" + application + noLang;
        }

        return null;
    }
    
    private static URL getResource(ServletContext sc, String uri) {
    	try {
			return sc.getResource(uri);
		} catch (MalformedURLException e) {
			return null;
		}
    }
}

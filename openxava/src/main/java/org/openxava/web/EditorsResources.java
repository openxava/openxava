package org.openxava.web;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * Utility used from JSP files to include JS and CSS files for editors.
 * 
 * Between 4.5 and 6.6 it was named EditorsJS.
 *  
 * @since 7.0
 * @author Javier Paniza 
 */
public class EditorsResources {
	
	private static Log log = LogFactory.getLog(EditorsResources.class);
	private static List<String> cssFiles;
	private static List<String> jsFiles; 
	
	public static Collection<String> listCSSFiles(String realPath) { 
		if (cssFiles == null) {
			cssFiles = new ArrayList<>();
			fillFilesFromFileSystem(cssFiles, realPath, "style", "css");
			fillFilesFromJar(cssFiles, "style", "css"); 
		}
		return cssFiles;
	}
	
	public static Collection<String> listJSFiles(String realPath) { 
		if (jsFiles == null) {
			jsFiles = new ArrayList<>();
			fillFilesFromFileSystem(jsFiles, realPath, "js", "js");
			fillFilesFromJar(jsFiles, "js", "js"); 
		}
		return jsFiles;
	}
	
	private static void fillFilesFromJar(List<String> result, String folder, String extension) {  
		try {
			Enumeration<URL> e = EditorsResources.class.getClassLoader().getResources("META-INF/resources/xava/editors/" + folder);
			while (e.hasMoreElements()) {
				URL url = e.nextElement();
				if (url.getProtocol().equals("jar")) {
					String jarURL = url.getFile().replace("file:", "");
					jarURL = Strings.noLastTokenWithoutLastDelim(jarURL, "!");
					ZipFile zip = new ZipFile(jarURL.replaceAll("%20", " "));
					try {
						Enumeration<? extends ZipEntry> entries = zip.entries();
						while (entries.hasMoreElements()) {
							ZipEntry entry = entries.nextElement();
							if (entry.getName().startsWith("META-INF/resources/xava/editors/" + folder + "/") && entry.getName().endsWith("." + extension)) {
								String name = entry.getName().replace("META-INF/resources/xava/editors/", ""); 
								if (!result.contains(name)) result.add(name);
							}
						}
					}
					finally {
						if (zip != null) zip.close();
					}
				}
			}
			java.util.Collections.sort(result);
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("editors_resources_not_loaded", extension.toUpperCase()), ex);
		}
	}
	
	private static void fillFilesFromFileSystem(Collection<String> result, String realPath, String folder, String extension) {  
		File resourcesFolder = new File(realPath + "/xava/editors/" + folder);		
		String[] resources = resourcesFolder.list();
		if (resources == null) return;
		Arrays.sort(resources);
		for (int i = 0; i < resources.length; i++) {
			if (resources[i].endsWith("." + extension)) {
				String name = folder + "/" + resources[i]; 
				if (!result.contains(name)) result.add(name);
			}
		}
	}

}

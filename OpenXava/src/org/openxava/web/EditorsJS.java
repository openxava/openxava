package org.openxava.web;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * Utility used from JSP files to include JS files for editors. 
 *  
 * @since 4.5
 * @author Javier Paniza 
 */
public class EditorsJS {
	
	private static Log log = LogFactory.getLog(EditorsJS.class);
	
	public static Collection listJSFiles(String realPath) {
		if (realPath == null) return listJSFilesFromProperties(); // To work in Tomcat with unpackWARs="false" 
		return listJSFilesFromFileSystem(realPath); // To work with WTP, where no ant task is called
	}

	private static Collection listJSFilesFromFileSystem(String realPath) {
		File jsEditorsFolder = new File(realPath + "/xava/editors/js");		
		String[] jsEditors = jsEditorsFolder.list();
		Arrays.sort(jsEditors);
		Collection result = new ArrayList();
		for (int i = 0; i < jsEditors.length; i++) {
			if (jsEditors[i].endsWith(".js")) {
				result.add(jsEditors[i]);
			}
		}
		return result;		
	}
	
	private static Collection listJSFilesFromProperties() {
		PropertiesReader reader = new PropertiesReader(EditorsJS.class, "editors-js.properties");		
		try {
			return reader.get().keySet();
		} 
		catch (IOException ex) {
			log.warn(XavaResources.getString("editors_js_not_loaded"), ex);
			return java.util.Collections.EMPTY_LIST;
		}			
	}	

}

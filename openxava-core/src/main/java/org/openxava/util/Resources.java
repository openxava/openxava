package org.openxava.util;

import java.io.*;
import java.net.*;

/**
 * @author Javier Paniza
 */
public class Resources {	
		
	/**
	 * Load a resource from the classpath and return it in String format. <p>
	 * 
	 * @param baseClass Base class from start the search of resource. 
	 * @param resourceName 
	 */
	public static String loadAsString(Class baseClass, String resourceName) throws IOException {
		URL resource = baseClass.getClassLoader().getResource(resourceName);		
		if (resource == null) {
			throw new IOException(XavaResources.getString("resource_not_found", resourceName));
		}
		return loadAsString(resource.openStream());		
	}

	/**
	 * Load a resource from the file system and return it in String format. <p>
	 */ 
	public static String loadAsString(String fileName) throws IOException {
		return loadAsString(new FileInputStream(fileName));		
	}
	
	private static String loadAsString(InputStream is) throws IOException {		
		StringBuffer sb = new StringBuffer();
		byte [] buf = new byte[500];
		int c = is.read(buf);
		while (c >= 0) {
			sb.append(new String(buf, 0, c));
			c = is.read(buf);
		}
		return sb.toString();
	}
	
	/**
	 * Load the resource as a stream from classpath, looking for all path prefixes until find it in one.
	 * 
	 * For example, the next code:<br>
	 * <code>Resources.getAsStreamInPrefixes("Customer.jrxml", "/reports/", "/informes/", "/")</code>
	 * 
	 * Look for /reports/Customer.jrxml, if not found for /informes/Customer.jrmxl, 
	 * if not found for /Customer.jrxml.
	 *  
	 * @param resourceName  The name of the resource in classpath, a file name.
	 * @param prefixes  A list of path prefixes, to look for each one. 
	 * @return The stream to get resource content, or null if not found.
	 * @since 7.0
	 */
	public static InputStream getAsStreamInPrefixes(String resourceName, String ... prefixes) { 
		for (String prefix: prefixes) {
			InputStream stream = Resources.class.getResourceAsStream(prefix + resourceName);
			if (stream != null) return stream;
		}
		return null;
	}
	
}

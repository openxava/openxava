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
	
}

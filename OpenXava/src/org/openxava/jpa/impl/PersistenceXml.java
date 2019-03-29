package org.openxava.jpa.impl;

import java.io.*;
import java.net.*;
import java.util.*;

import org.openxava.jpa.*;

/**
 * To obtain the URL of the persistence.xml to be used in this application. <p>
 *
 * @since 4.7
 * @author Javier Paniza 
 */
public class PersistenceXml {
	
	/**
	 *  Returns the URL of the persistence.xml to be used in this application. <p>
	 */
	public static URL getResource() throws IOException {  
		Enumeration e = XPersistence.class.getClassLoader().getResources("META-INF/persistence.xml");
		URL url = null;
		while (e.hasMoreElements()) {
			url = (URL) e.nextElement();
			if (url.toExternalForm().contains("/WEB-INF/classes/META-INF")) return url; 
		}		
		return url;
	}

}

package org.openxava.jpa.impl;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.xml.parsers.*;

import org.apache.commons.logging.*;
import org.openxava.jpa.*;
import org.w3c.dom.*;

/**
 * To work with the persistence.xml to be used in this application. <p>
 *
 * @since 4.7
 * @author Javier Paniza 
 */
public class PersistenceXml {

	private static Log log = LogFactory.getLog(PersistenceXml.class); 
	
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
	
	/**
	 * @since 6.1.1
	 */
	public static String getPropetyValue(String persistenceUnit, String propertyName) throws ParserConfigurationException {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			URL url = getResource();
			Document doc = builder.parse(url.toExternalForm());
			NodeList units = doc.getElementsByTagName("persistence-unit");
			int unitsCount = units.getLength();
			for (int i=0; i<unitsCount; i++) {
				Element unit = (Element) units.item(i);
				if (persistenceUnit.equals(unit.getAttribute("name"))) {																
					NodeList nodes = unit.getElementsByTagName("property");
					int length = nodes.getLength(); 
					for (int j=0; j<length; j++) {
						Element el = (Element) nodes.item(j);
						String name = el.getAttribute("name");
						if (propertyName.equals(name)) {
							return el.getAttribute("value");
						}
					}
				}				
			}
			return null;
		}
		catch (ParserConfigurationException ex) {
			throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex); 
			throw new ParserConfigurationException(ex.getMessage());
		}
	}


}

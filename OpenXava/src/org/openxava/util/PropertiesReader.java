package org.openxava.util;

import java.io.*;
import java.net.*;
import java.util.*;




/**
 * Reads properties files. <p>
 *
 * @author: Javier Paniza
 */
public class PropertiesReader {

	private Class theClass;
	private String propertiesFileURL;
	private Properties properties;
	
	
	
	/**
	 * @param propertiesFileURL  Cannot be null
	 * @param theClass  Class from obtain the <code>ClassLoader</code> used to read the file. Cannot be nul
	 */
	public PropertiesReader(Class theClass, String propertiesFileURL) {
		Assert.arg(theClass, propertiesFileURL);
		this.theClass = theClass;
		this.propertiesFileURL = propertiesFileURL;
	}
	
  // Adds properties in url to p.
  private void add(Properties p, URL url) throws IOException {
		// assert(p, url);	     	  	
		InputStream is = url.openStream();
		Properties properties = new Properties();
		properties.load(is);
		p.putAll(properties);
		try { is.close(); } catch (IOException ex) {}
  }
  
  /**
   * Returns properties associated to indicated file. <p> 
   * 
   * Read all files in classpath with the property file name used in constructor.
   * The result is a mix of all properties of this files. <br>
   * Only read the first time. <br>
   *
   * @return Not null
   */
  public Properties get() throws IOException {
	  if (properties == null) {		
			Enumeration e = theClass.getClassLoader().getResources(propertiesFileURL);
			properties = new Properties();
			List urls = new ArrayList();
			List priorityURLs = new ArrayList();
			while (e.hasMoreElements()) {
				URL url = (URL) e.nextElement();
				// We give priority to WEB-INF/classes, we do not trust in the classloader configuration of the application server
				boolean priority = Strings.noLastToken(url.toExternalForm(), "/").endsWith("/WEB-INF/classes/");
				if (priority) priorityURLs.add(url);
				else urls.add(url);
			}		
			
			Collections.reverse(urls);
			Collections.reverse(priorityURLs);
			urls.addAll(priorityURLs);
			
			for (Iterator it = urls.iterator(); it.hasNext(); ) {
				URL url = (URL) it.next();
				add(properties, url);
			}
			
		}
		return properties;
  }          
  
}

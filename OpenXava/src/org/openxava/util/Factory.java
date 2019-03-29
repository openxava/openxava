package org.openxava.util;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.commons.logging.*;

/**
 * Generic class to implement object factories. <p>
 *
 * The classes are in a properties file with <i>name=class</i> formate.
 * Where <i>nombre</i> is a identifier and <i>class</i> the class
 * of object to create, which must to have a default constructor.<p>
 * 
 * If the class implements {@link IInit} then the method {@link IInit#init} is
 * called after creation.<br>
 * Exists a entry that is assumed as default entry. This entry is used
 * when you call to <code>create()</code> without arguments.<br>
 * The default entry can be defined on create the factory or
 * if not the entry <i>_default/_defecto</i> is assumed. <br>
 *
 * A example of properties file is:
 * <pre>
 * _defecto=rapido
 * rapido=miempresa.miapp.paquete1.MiClaseRapida
 * lento=miempresa.miapp.paquete1.MiClaseLenta
 * </pre>
 * Or in english
 * _default=fast
 * fast=mycorp.myapp.package1.MyFastClass
 * slow=mycorp.myapp.package1.MySlowClass
 * 
 * @author  Javier Paniza
 */

public class Factory {

  // On changing this final var change heading and constructor doc
  private final static String DEFAULT_ENTRY_ES = "_defecto";
  private final static String DEFAULT_ENTRY_EN = "_default";

  private static Log log = LogFactory.getLog(Factory.class);
  
  private URL propertiesFile;
  private String defaultEntry = null; // entry that indicate what entry is by default
  private String theDefault; // name of default entry, is obtained from defaultEntry

  private Hashtable classes = new Hashtable(); // of Class
  private Properties properties;
  
  

  /**
   * Creates a objects factory from a properties files. <p>
   * 
   * The entry that indicate the default entry is <i>_defecto</i> or <i>_default</i>.<br>
   * 
   * <b>Preconditions:</b>
   * <ul>
   * <li> propertiesFile != null
   * </ul>
   * @param propertiesFile  File URL withn pairs names/classes
   */
  public Factory(URL propertiesFile) {
  	this(propertiesFile, null);
  }
  
  /**
   * Creates a object factory from a properties file, sending the entry
   * that indicate the default entry. <p>
   * 
   * <b>Preconditions:</b>
   * <ul>
   * <li> propertiesFile != null
   * </ul>
   * @param propertiesFile  File URL with pairs names/classes
   * @param defaultEntry  Indicante what entry will be used as default entry, si
   *                 if is <code>null</code> <i>_defecto/_default</i> is used.
   */
  public Factory(URL propertiesFile, String defaultEntry) {
		if (propertiesFile == null) {
		  throw new IllegalArgumentException(XavaResources.getString("properties_file_not_valid"));
		}
		this.propertiesFile = propertiesFile;
		if (defaultEntry != null) this.defaultEntry = defaultEntry;
  }
  
  /**
   * Create a default object. <p>
   * 
   * <b>Postcondition:</b>
   * <ul>
   * <li> return != null
   * </ul>
   * does it as indicate in the entry default in properties file.
   *  
   * @exception InitException  Some problem on init
   */
  public Object create() throws InitException {
  	return create(getDefault());
  }
  
  /**
   * Create the indicate object. <p>
   * 
   * <b>Postcondition:</b>
   * <ul>
   * <li> return != null
   * </ul>
   * @param name  Identifier name, which is registered in properties
   * @exception InitException  Si hay algún problema el iniciar.
   */
  public Object create(String name) throws InitException {
		try {
		  Object rs = getClase(name).newInstance();
		  if (rs instanceof IInit) {
				IInit ini = (IInit) rs;
				ini.init(name);
		  }
		  return rs;
		}
		catch (InitException ex) {
		  throw ex;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		  throw new InitException("factory_create_error", name);
		}
  }
  
  /**
   * Returns the class associated to indicated name. <p>
   * 
   * The classes is stored for reused.<br>
   */
  private Class getClase(String name) throws Exception {
  	Class theClass = (Class) classes.get(name);
  	if (theClass == null) {
		  String className = getProperties().getProperty(name);
		  if (className == null) {
		  	throw new InitException("factory_class_not_found", name, propertiesFile);
		  }
		  theClass = Class.forName(className);
		  classes.put(name, theClass);
		}
  	return theClass;
  }
  
  /**
   * Returns the default entry, obtained from <tt>defaultEntry</tt>.
   */
  private String getDefault() throws InitException {
  	if (theDefault == null) {
  		try {
  				if (defaultEntry == null) {
  					theDefault = getProperties().getProperty(DEFAULT_ENTRY_EN);
  					if (theDefault == null) theDefault = getProperties().getProperty(DEFAULT_ENTRY_ES); 
  				}
  				else {
  					theDefault = getProperties().getProperty(defaultEntry);
  				}
  				if (theDefault == null) {
  					throw new InitException("factory_entry_not_found", defaultEntry, propertiesFile);
  				}
  		}
  		catch (IOException ex) {
  			log.error(ex.getMessage(), ex);
  			throw new InitException("properties_file_read_error");
  		}
  	}
  	return theDefault;
  }
  
  /**
   * Reads the properties file and convert it to a <code>Properties</code>. <p>
   */
  private Properties getProperties() throws IOException {
		if (properties == null) {
		  InputStream is = propertiesFile.openStream();
		  properties = new Properties();
		  properties.load(is);
		  try { is.close(); } catch (IOException ex) {}
		}
		return properties;
  }
  
}

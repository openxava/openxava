package org.openxava.util;


/**
 * Exception thrown by  {@link PropertiesManager}. <p>
 *
 * @author  Javier Paniza
 */

public class PropertiesManagerException extends Exception {


  public PropertiesManagerException() {
	  super(XavaResources.getString("property_manager_exception"));
  }  
  
  public PropertiesManagerException(String mensaje) {
  	super(mensaje);
  }
  
}

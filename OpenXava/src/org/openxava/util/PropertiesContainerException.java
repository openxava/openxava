package org.openxava.util;


/**
 * tmp
 * Exception thrown by  {@link IPropertiesContainer}. <p>
 *
 * @author  Javier Paniza
 */

public class PropertiesContainerException extends XavaException {


  public PropertiesContainerException() {
	  super();
  }  
  
  public PropertiesContainerException(String messageId) {
  	super(messageId);
  }
  
  public PropertiesContainerException(String messageId, Object argv0) {
	super(messageId, argv0);
  }

  
}

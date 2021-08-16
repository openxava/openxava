package org.openxava.util;


/**
 * Exception thrown by  {@link IPropertiesContainer}. <p>
 *
 * @since 6.5.2
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

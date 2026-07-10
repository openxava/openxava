package org.openxava.util;

import java.util.*;

import org.openxava.validators.*;


/**
 * Allows update and read object properties to/from a Map. <p> 
 *
 * @author  Javier Paniza
 */

public interface IPropertiesContainer {

  /**
   * Obtain the values of properties in a Map. <p> 
   *
   * @param properties  Names of properties to obtain separated by 
   * 		a colon (:). The properties have to exists in the object.
   * @return Map with<tt>String propertyName:Object value</tt>. Not null
   * @exception PropertiesContainerException  Any unexpected problem
   * 						
   */
  Map executeGets(String properties) throws PropertiesContainerException; 
  
  /**
   * Update the properties from a map. <p> 
   *
   * @param properties Map with <tt>String propertyName:Object value</tt>. Null is like a empty map.
   *                               
   * @exception ValidationException  Some problem validating the data
   * @exception PropertiesContainerException  Any unexpected problem
   */
  void executeSets(Map properties) throws ValidationException, PropertiesContainerException;  
  
}

package org.openxava.util;

import java.rmi.*;
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
   * @exception RemoteException  Some system problem or another unexpected problem
   */
  Map executeGets(String properties) throws RemoteException;
  
  /**
   * Update the properties from a map. <p> 
   *
   * @param properties Map with <tt>String propertyName:Object value</tt>. Null is like a empty map.
   *                               
   * @exception ValidationException  Some problem validating the data
   * @exception RemoteException  Some system problem or another unexpected problem
   */
  void executeSets(Map properties) throws ValidationException, RemoteException;
  
}

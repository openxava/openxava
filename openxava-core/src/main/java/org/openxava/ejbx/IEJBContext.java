package org.openxava.ejbx;

import java.security.*;

import org.openxava.util.*;


/**
 * Find typical resources to implement a EJB. <p>
 * 
 * Isolate differences between EJB 1.0 and EJB 1.1(or 2.x), differences
 * between distincts EJB 1.0, and it smooths EJB API.
 * It better to use <code>IEJBContext</code> versus direct access to
 * resource, thus we have more portability. <br>
 *
 * Object of this type can be obtained  
 * Los objetos de este tipo se obtendrán by means of {@link EJBContextFactory}.<br>
 *
 * The {@link IConnectionProvider#getConnection()} method without arguments
 * obtain the connection of the property bean named DATA_SOURCE, that it have
 * to point at a valida datasource.<br>
 * It's important follow this norm for sure the compatability of bean code
 * between differents ejb server versions.<br> 
 *
 * Also it's possible to set the default connection using 
 * {@link IConnectionProvider#setDefaultDataSource}.
 *  
 * @author  Javier Paniza
 */

public interface IEJBContext extends IConnectionProvider {

  /** As <code>EJBContext.getCallerPrincipal</code> in EJB 1.1. */
  Principal getCallerPrincipal();
  
  /**
   * Property value from name. <br>
   * Returns <code>null</code> if the property
   * does not exist or it is not possible obtain it.<br>
   * If the property exists but it does not have value
   * then return empty string.<br>
   */
  String getProperty(String name);
  
  /** As <code>EJBContext.isCallerInRole</code> in EJB 1.1. */
  boolean isCallerInRole(String roleName);
  
}

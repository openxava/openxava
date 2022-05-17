package org.openxava.ejbx;

import javax.ejb.*;

/**
 * Add the possibility of init to {@link IEJBContext}. <p> 
 *
 * It's a convenience interface for implementation, if you
 * create a class that you want use as <code>IEJBContext</code>
 * you must to implement this interface, although the final
 * user does not know about it.<br>
 * Also it's important that implementing class have a 
 * default constructor, but without logic. <br>
 * 
 * @author  Javier Paniza
 */

public interface IEJBContextInit extends IEJBContext {

  /**
   * Sets a <code>EJBContext</code> needed for implementing. <br>
   * It's required call to this method so <code>IEJBContext</code> can be used. 
   */
  void setEJBContext(EJBContext ejbContext);
  
}

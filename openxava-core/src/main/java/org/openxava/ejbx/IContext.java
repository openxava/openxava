package org.openxava.ejbx;

import javax.naming.*;


/**
 * Look a resource from its name. <p>
 * 
 * It's of generic use, and more easy to implement than
 * JNDI one.
 *
 * @author  Javier Paniza
 */

public interface IContext {

  /**
   * Look a resource from its name. <br>
   * The returned object can be of any type. <br>
   */
  Object lookup(String nombre) throws NamingException;
  
  void close() throws NamingException;
}

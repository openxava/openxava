package org.openxava.ejbx;

import javax.ejb.*;



import org.openxava.util.*;


/**
 * It create <code>IEJBContext</code>s. <p>
 *
 * @author  Javier Paniza
 */


public class EJBContextFactory {

	

  /**
   * It create a <code>IEJBContext</code> used for implementing the <code>EJBContext</code> indicated. <br> 
   *
   * <b>Postcondition:</b>
   * <ul>
   * <li> return != null
   * </ul>
   * If you send <code>null</code> as argument you can cause the invariant of the created objects
   * with its consequences (throw of exception on use it). <br>
   */
  public static IEJBContext create(EJBContext ejbContext) throws InitException  {
		IEJBContextInit rs = new EJB11Context();
		rs.setEJBContext(ejbContext);
		return rs;
  }
  
}

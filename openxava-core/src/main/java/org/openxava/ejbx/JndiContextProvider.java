package org.openxava.ejbx;

import javax.naming.*;





/**
 * Provides a {@link IContext} that look up in JNDI space. <p>
 *
 * It don't make caché of returned context, it create one new each time. <br>
 * 
 * The look up is made using <tt>new InitialContext()</tt> of JNDI.
 *
 * @author  Javier Paniza
 */

public class JndiContextProvider implements IContextProvider {

  
  
  public IContext getContext() throws NamingException {
  	return new JndiContext(new InitialContext());
  }
  
}

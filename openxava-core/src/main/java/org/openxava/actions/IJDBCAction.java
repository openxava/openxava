package org.openxava.actions;

import org.openxava.util.*;

/**
 * @author Javier Paniza
 */
public interface IJDBCAction extends IAction {
	
	/**
	 * This method is call from the system to provide a
	 * JDBC connection provider. <p>
	 *
	 * When the <tt>execute</tt> method is called this
	 * method alredy has been called with a valid connection
	 * provider.<br> 
	 */
	public void setConnectionProvider(IConnectionProvider provider);
	
}

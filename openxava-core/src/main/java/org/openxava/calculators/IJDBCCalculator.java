package org.openxava.calculators;

import org.openxava.util.*;


/**
 * @author Javier Paniza
 */
public interface IJDBCCalculator extends ICalculator {
	
	/**
	 * This method is call from the system to provide a
	 * JDBC connection provider. <p>
	 *
	 * When the <tt>calculate</tt> method is called this
	 * method alredy has been called with a valid connection
	 * provider.<br> 
	 */
	public void setConnectionProvider(IConnectionProvider provider);

}

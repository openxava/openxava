package org.openxava.util;

/**
 * Problems cloning. <p>
 *
 * It's not like <tt>CloneNotSupportedException</tt> that indicate that
 * method <tt>clone</tt> is not supported. This excepction is thrown
 * when there are problems executing the clone process, although 
 * the clone method itself is supported. <br> 
 * 
 * @author Javier Paniza
 */
public class CloneException extends Exception {
	
	public CloneException() {
		super(XavaResources.getString("clone_exception"));
	}
	
	public CloneException(String s) {
		super(s);
	}
	
}

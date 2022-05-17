package org.openxava.util;

/**
 * Exception thrown when a assert is broken. <p>
 *
 * The break of a assert imply a programming error. Usually
 * this exception is not thrown directly but from 
 * {@link org.openxava.util.Assert} class.<br>
 * It si not mandatory throw this exception in case of programming error,
 * another possible exceptions are anothers <tt>RuntimeException</tt> associated
 * to the source problem, for example <tt>IndexOutOfBoundsException</tt>, 
 * <tt>IllegalArgumentException</tt> or the ominous <tt>NullPointerException</tt>. 
 * 
 * @author: Javier Paniza
 */
public class AssertException extends RuntimeException {
			
	public AssertException() {
		super(XavaResources.getString("assert_exception"));
	}

	public AssertException(String s) {
		super(s);
	}
	
}

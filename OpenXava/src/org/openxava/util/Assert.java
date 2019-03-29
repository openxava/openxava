package org.openxava.util;

import org.apache.commons.logging.*;

/**
 * Utility to make asserts. <p>
 *
 * In most case this asserts verify if the object is null.
 * There are a variety of methods that throw differents exceptions.<br>
 * One of the thrown exception is <tt>AssertException</tt>, but this
 * is not mandatory.<p>
 *
 * If some of this methods throw exception this is usually
 * due to a software error, normally a exception that the
 * user-programmer has not fullfilled the contract. <br>
 * The messages does not have to specify the place where
 * contract is break or which contract is broken. The
 * trace (that always is printed) indicate the place and
 * the exception class. In the doc of method that throw the
 * exception you can obtain more info about it.<br>
 *
 * The names and behaviour match with JUnit,
 * the difference is the thrown exceptions. <br>
 *
 * @author  Javier Paniza
 */

public class Assert {

  private static Log log = LogFactory.getLog(Assert.class);
	
  /**
   * Verify argument. <br>
   *
   * @exception IllegalArgumentException If <tt>o == null</tt>.
   */
  public final static void arg(Object o) {
		if (o == null) {
		  throwException(new IllegalArgumentException(XavaResources.getString("assert_no_null_argv")));
		}
  }
  
  /**
   * Verify arguments. <br>
   *
   * @exception IllegalArgumentException If <tt>o1 == null || o2 == null</tt>.
   */
  public final static void arg(Object o1, Object o2) {
		if (o1 == null || o2 == null) {
		  throwException(new IllegalArgumentException(XavaResources.getString("assert_no_null_argv")));
		}
  }
  
  /**
   * Verify arguments. <br>
   *
   * @exception IllegalArgumentException If <tt>o1 == null || o2 == null || o3 == null</tt>.
   */
  public final static void arg(Object o1, Object o2, Object o3) {
		if (o1 == null || o2 == null || o3 == null) {
		  throwException(new IllegalArgumentException(XavaResources.getString("assert_no_null_argv")));
		}
  }
  
  /**
   * Verify arguments. <br>
   *
   * @exception IllegalArgumentException If <tt>o1 == null || o2 == null || o3 == null || o4 == null</tt>.
   */
  public final static void arg(Object o1, Object o2, Object o3, Object o4) {
	if (o1 == null || o2 == null || o3 == null || o4 == null) {
	  throwException(new IllegalArgumentException(XavaResources.getString("assert_no_null_argv")));
	}
  }
  
  /**
   * Verify arguments. <br>
   *
   * @exception IllegalArgumentException If <tt>o1 == null || o2 == null || o3 == null || o4 == null || o5 == null</tt>.
   */
  public final static void arg(Object o1, Object o2, Object o3, Object o4, Object o5) {
	if (o1 == null || o2 == null || o3 == null || o4 == null || o5 == null) {
	  throwException(new IllegalArgumentException(XavaResources.getString("assert_no_null_argv")));
	}
  }
  
  /**
   * Verify arguments. <br>
   *
   * @exception IllegalArgumentException If <tt>o1 == null || o2 == null || o3 == null || o4 == null || o5 == null || o6 == null</tt>.
   */
  public final static void arg(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
	if (o1 == null || o2 == null || o3 == null || o4 == null || o5 == null || o6 == null) {
	  throwException(new IllegalArgumentException(XavaResources.getString("assert_no_null_argv")));
	}
  }
  
  /**
   * Verify arguments. <br>
   *
   * @since 5.9
   * @exception IllegalArgumentException If <tt>o1 == null || o2 == null || o3 == null || o4 == null || o5 == null || o6 == null || o7 == null</tt>.
   */
  public final static void arg(Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) { 
	if (o1 == null || o2 == null || o3 == null || o4 == null || o5 == null || o6 == null || o7 == null) {
	  throwException(new IllegalArgumentException(XavaResources.getString("assert_no_null_argv")));
	}
  }

  
  
  
	/**
	 * Asserts that a condition is true. <p>
	 *
	 * @exception AssertException  If condition is false	 
	 */
	static public void assertTrue(String message, boolean condition) {
		if (!condition)
			fail(message);
	}
	
	/**
	 * Asserts that a condition is true. <p>
	 *
	 * @exception AssertException  If condition is false
	 */
	static public void assertTrue(boolean condition) {
		assertTrue(null, condition);
	}
	/**
	 * Asserts that two doubles are equal.
	 * @param expected the expected value of an object
	 * @param actual the actual value of an object
	 * @param delta tolerated delta
	 * @exception AssertException  If assert is not fulfilled	 
	 */
	static public void assertEquals(double expected, double actual, double delta) {
	    assertEquals(null, expected, actual, delta);
	}
	/**
	 * Asserts that two longs are equal.
	 * @param expected the expected value of an object
	 * @param actual the actual value of an object
	 * @exception AssertException  If assert is not fulfilled	 
	 */
	static public void assertEquals(long expected, long actual) {
	    assertEquals(null, expected, actual);
	}
	/**
	 * Asserts that two objects are equal. <p>
	 *
	 * @param expected the expected value of an object
	 * @param actual the actual value of an object
	 * @exception AssertException  If assert is not fulfilled	 
	 */
	static public void assertEquals(Object expected, Object actual) {
	    assertEquals(null, expected, actual);
	}
	/**
	 * Asserts that two doubles are equal.
	 * @param message the detail message for this assertion
	 * @param expected the expected value of an object
	 * @param actual the actual value of an object
	 * @param delta tolerated delta
	 * @exception AssertException  If assert is not fulfilled
	 */
	static public void assertEquals(String message, double expected, double actual, double delta) {
		if (!(Math.abs(expected-actual) <= delta)) // Because comparison with NaN always returns false
			failNotEquals(message, new Double(expected), new Double(actual));
	}
	/**
	 * Asserts that two longs are equal.
	 * @param message the detail message for this assertion
	 * @param expected the expected value of an object
	 * @param actual the actual value of an object
	 * @exception AssertException  If assert is not fulfilled	 
	 */
	static public void assertEquals(String message, long expected, long actual) {
	    assertEquals(message, new Long(expected), new Long(actual));
	}
	/** 
	 * Asserts that two objects are equal. If they are not
	 * an AssertionFailedError is thrown.
	 * @param message the detail message for this assertion
	 * @param expected the expected value of an object
	 * @param actual the actual value of an object
	 * @exception AssertException  If assert is not fulfilled	 
	 */
	static public void assertEquals(String message, Object expected, Object actual) {
		if (expected == null && actual == null)
			return;
		if (expected != null && expected.equals(actual))
			return;
		failNotEquals(message, expected, actual);
	}
	
	/**
	 * Asserts that an object isn't null.
	 *
	 * @exception AssertException  If assert is not fulfilled	 
	 */
	static public void assertNotNull(Object object) {
		assertNotNull(null, object);
	}
	/**
	 * Asserts that an object isn't null.
	 *
	 * @exception AssertException  If assert is not fulfilled	 
	 */
	static public void assertNotNull(String message, Object object) {
		assertTrue(message, object != null); 
	}
	/**
	 * Asserts that an object is null.
	 *
	 * @exception AssertException  If assert is not fulfilled	 
	 */
	static public void assertNull(Object object) {
		assertNull(null, object);
	}
	/**
	 * Asserts that an object is null.
	 *
	 * @exception AssertException  If assert is not fulfilled
	 */
	static public void assertNull(String message, Object object) {
		assertTrue(message, object == null); 
	}
	/**
	 * Asserts that two objects refer to the same object. <p>
	 *
	 * @param expected the expected value of an object
	 * @param actual the actual value of an object
	 * @exception AssertException  If assert is not fulfilled	 
	 */
	static public void assertSame(Object expected, Object actual) {
	    assertSame(null, expected, actual);
	}
	/**
	 * Asserts that two objects refer to the same object. <p>
	 *
	 * @param message the detail message for this assertion
	 * @param expected the expected value of an object
	 * @param actual the actual value of an object
	 * @exception AssertException  If assert is not fulfilled	 
	 */
	static public void assertSame(String message, Object expected, Object actual) {
		if (expected == actual)
			return;
		failNotSame(message, expected, actual);
	}
	
	/**
	 * Throws a AssertException and print the trace. <p>
	 */
	public final static void fail() {
		throwException(new AssertException());
	}
	
	/**
	 * Throws a AssertException and print the trace. <p>
	 */
	public final static void fail(String message) {
		throwException(new AssertException(message));
	}
	static private void failNotEquals(String message, Object expected, Object actual) {
		String formatted= "";
		if (message != null)
			formatted= message+" ";
		fail(formatted+ XavaResources.getString("expected_but_was", expected, actual));
	}
	static private void failNotSame(String message, Object expected, Object actual) {
		String formatted= "";
		if (message != null)
			formatted= message+" ";
		fail(formatted+ XavaResources.getString("expected_same"));
	}
	
  // Throws exception and print stack trace
  private static void throwException(RuntimeException ex) {
		try {
		  throw ex;
		}
		catch (RuntimeException ex2) {
			log.error(ex.getMessage(), ex);
		  throw ex2;
		}
  }
  
}

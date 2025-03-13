package org.openxava.test.util;

/**
 * A simple static counter utility for testing purposes.
 * Used to count method calls across different instances.
 * 
 * @author Javier Paniza
 */
public class StaticCounter {
	
	private static int count = 0;
	
	/**
	 * Increments the counter by one.
	 */
	public static void increment() {
		count++;
	}
	
	/**
	 * Resets the counter to zero.
	 */
	public static void reset() {
		count = 0;
	}
	
	/**
	 * Returns the current value of the counter.
	 * 
	 * @return The current count value
	 */
	public static int getValue() {
		return count;
	}
}

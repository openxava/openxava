package org.openxava.validators.hibernate;

/**
 * 
 * @author Javier Paniza 
 */
public class HibernateValidatorInhibitor {
	
	final private static ThreadLocal<Boolean> inhibited = new ThreadLocal<Boolean>();
	
	public static boolean isInhibited() {
		Boolean result = inhibited.get();
		return result==null?false:result;
	}
	
	public static void setInhibited(boolean inhibited) {
		HibernateValidatorInhibitor.inhibited.set(inhibited);
	}

}

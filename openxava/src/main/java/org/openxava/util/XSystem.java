package org.openxava.util;

import java.util.*;

import org.apache.commons.logging.*;

/**
 * Global utilities about the system. <p>
 * 
 * @author Javier Paniza
 */

public class XSystem {

	private static Log log = LogFactory.getLog(XSystem.class);
	
	private static boolean onServer = false;
	
	/**
	 * Does that {@link #onServer} returns <tt>true</tt>.
	 *
	 * Must to be called from a static block in a common base class of EJB, or
	 * in all EJB is there aren't base class.<br>
	 */
	public static void _setOnServer() {
		onServer = true;
	}
	
	/**
	 * If we are in a client: java application, applet, servlet, jsp, etc. <p> 
	 */
	public static boolean onClient() {
		return !onServer();
	}
	
	/**
	 * If we are in a EJB server. <p>
	 */
	public static boolean onServer() {
		return onServer;
	}

	
	/**
	 * To use for XML encoding and for web requests and responses encoding. <p>
	 */
	public static String getEncoding() {
		// Before v7.1.3 we had a intricate algorithm to decide the encoding to use 
		return "UTF-8"; 
	}
	
	/** @since 6.0 */
	public static boolean isJava9orBetter() {
		return isEqualOrBetterThanJavaVersion(9);
	}
	
	/** @since 7.3 */
	public static boolean isJava17orBetter() {
		return isEqualOrBetterThanJavaVersion(17);
	}
	
	/** @since 7.3.1 */
	public static boolean isJava21orBetter() {
		return isEqualOrBetterThanJavaVersion(21);
	}
	
	private static boolean isEqualOrBetterThanJavaVersion(int javaVersion) {
		try {
			String version = System.getProperty("java.specification.version");
			return Integer.parseInt(version) >= javaVersion;
		}
		catch (NumberFormatException ex) {
			// Because because 1.6, 1.7 and 1.8 as value
			return false;
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("determine_is_java_version_problem", javaVersion), ex);
			return false;
		}
	}
}

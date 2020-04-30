package com.openxava.naviox.model;

/**
 *
 * @since 5.4
 * @author Javier Paniza
 */

public class Configuration {
	
	private static Configuration instance;
	
	public static Configuration getInstance() {
		if (instance == null) {
			instance = new Configuration();
		}
		return instance;
	}
	
	public int getLockSessionMilliseconds() { 
		return 0; 
	}	
	
	public boolean isSharedUsersBetweenOrganizations() {
		return true;
	}
}

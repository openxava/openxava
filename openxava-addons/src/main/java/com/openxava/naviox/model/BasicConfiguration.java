package com.openxava.naviox.model;

/**
 * 
 * @since 7.0
 * @author Javier Paniza
 */

public class BasicConfiguration extends Configuration {
	
	public int getLockSessionMilliseconds() { 
		return 0; 
	}	
	
	public boolean isSharedUsersBetweenOrganizations() {
		return true;
	}
}

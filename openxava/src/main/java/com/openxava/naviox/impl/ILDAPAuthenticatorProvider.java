package com.openxava.naviox.impl;

/**
 * tmr doc
 * 
 * @since 7.4
 * @author Javier Paniza
 */

public interface ILDAPAuthenticatorProvider {
	
	boolean isValidLogin(String user, String password); // tmr doc

}

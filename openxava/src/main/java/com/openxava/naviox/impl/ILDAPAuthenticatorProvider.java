/*
 * NaviOX. Navigation and security for OpenXava applications.
 * Copyright 2014 Javier Paniza Lucas
 *
 * License: Apache License, Version 2.0.	
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

package com.openxava.naviox.impl;

/**
 * To define your own user authentication logic against your LDAP server. <p>
 * 
 * Sometimes the default logic that XavaPro uses to authenticate against the LDAP server is not sufficient,
 * you may want to authenticate against multiple LDAP servers, a special LDAP server that requires special code, etc.
 * 
 * Create a class that implement this interface and add an entry in naviox.properties of your project, like:
 * <pre>
 * ldapAuthenticatorProviderClass=com.mycompany.myapp.impl.MyCustomLDAPAuthenticatorProvider 
 * </pre>
 * 
 * @since 7.4
 * @author Javier Paniza
 */

public interface ILDAPAuthenticatorProvider {
	
	/** Return true if it achieves to connect to the LDAP server with the user and password indicated. */
	boolean isValidLogin(String user, String password); 

}

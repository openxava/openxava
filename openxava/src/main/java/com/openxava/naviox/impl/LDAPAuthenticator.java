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

import org.apache.commons.logging.*;
import org.openxava.util.*;

import com.openxava.naviox.util.*;

/**
 * 
 * @since 7.4
 * @author Javier Paniza
 */
public class LDAPAuthenticator {
	
	private static Log log = LogFactory.getLog(ModulesHelper.class);	
	private static ILDAPAuthenticatorProvider provider;	
	
	public static boolean isValidLogin(String user, String password) {
		return getProvider().isValidLogin(user, password);
	}
	
	private static ILDAPAuthenticatorProvider getProvider() {
		if (provider == null) {
			try {
				provider = (ILDAPAuthenticatorProvider) Class.forName(NaviOXPreferences.getInstance().getLDAPAuthenticatorProviderClass()).newInstance();
			} catch (Exception ex) {
				log.warn(XavaResources.getString("provider_creation_error", "LDAPAuthenticator"), ex);
				throw new XavaException("provider_creation_error", "LDAPAuthenticator");
			}
		}
		return provider;
	}

}

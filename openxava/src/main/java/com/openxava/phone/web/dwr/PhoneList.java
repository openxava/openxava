/*
 * XavaPhone. Mobile UI for OpenXava application (unimplemented).
 * Copyright 2014 Javier Paniza Lucas
 *
 * License: Apache License, Version 2.0.	
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

package com.openxava.phone.web.dwr;

import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

import com.openxava.naviox.impl.*;
import com.openxava.naviox.util.*;
import com.openxava.phone.impl.*;

/**
 * 
 * @author Jeromy Altuna
 * @since  5.8
 */
public class PhoneList {
	
	private static Log log = LogFactory.getLog(PhoneList.class);
	private static IPhoneListProvider provider;
	
	public String filter(HttpServletRequest request, HttpServletResponse response, String application, String module, String searchWord) {
		return getProvider().filter(request, response, application, module, searchWord);
	}
	
	private static IPhoneListProvider getProvider() {
		if (provider == null) {
			try {
				provider = (IPhoneListProvider) Class.forName(NaviOXPreferences.getInstance().getPhoneListProviderClass()).newInstance();
			} 
			catch (Exception ex) {
				log.warn(XavaResources.getString("provider_creation_error", "PhoneList"), ex);
				throw new XavaException("provider_creation_error", "PhoneList");
			}
		}
		return provider;
	}

}
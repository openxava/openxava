package com.openxava.phone.web.dwr;

import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

import com.openxava.naviox.impl.*;
import com.openxava.naviox.util.*;

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
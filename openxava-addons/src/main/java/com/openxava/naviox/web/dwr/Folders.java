package com.openxava.naviox.web.dwr;

import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

import com.openxava.naviox.impl.*;
import com.openxava.naviox.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class Folders { 
	
	private static Log log = LogFactory.getLog(Folders.class);
	private static IFoldersProvider provider;
	
	public String goFolder(HttpServletRequest request, HttpServletResponse response, String folderOid) {
		return getProvider().goFolder(request, response, folderOid);
	}
	
	public String goBack(HttpServletRequest request, HttpServletResponse response) {
		return getProvider().goBack(request, response);
	}
	
	private static IFoldersProvider getProvider() {
		if (provider == null) {
			try {
				provider = (IFoldersProvider) Class.forName(NaviOXPreferences.getInstance().getFoldersProviderClass()).newInstance();
			} 
			catch (Exception ex) {
				log.warn(XavaResources.getString("provider_creation_error", "Folders"), ex);
				throw new XavaException("provider_creation_error", "Folders");
			}
		}
		return provider;
	}

	
}
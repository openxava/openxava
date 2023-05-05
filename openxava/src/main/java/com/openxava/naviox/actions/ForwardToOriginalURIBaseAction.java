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

package com.openxava.naviox.actions;

import org.openxava.actions.*;
import org.openxava.util.*;

import com.openxava.naviox.impl.*;

/**
 * 
 * @author Javier Paniza 
 */
abstract public class ForwardToOriginalURIBaseAction extends ViewBaseAction implements IForwardAction {
	
	private String forwardURI = null;

	protected void forwardToOriginalURI() throws Exception {
		String originalURI = null;
		String originalParameters = null;
		String originalURL = (String) getRequest().getSession().getAttribute("naviox.originalURL");
		if (originalURL != null) {
			getRequest().getSession().removeAttribute("naviox.originalURL");
			String [] url = originalURL.split("\\?");
			originalURI = url[0];
			if (url.length > 1) {
				originalParameters = url[1].replace("originalParameters=", ""); 
			}
		}
		if (originalURI == null) {
			forwardURI = "/";
		}
		else {
			int idx = originalURI.indexOf("/", 1);			
			if (!originalURI.endsWith("/SignIn") && idx > 0 && idx < originalURI.length()) {
				forwardURI = originalURI.startsWith("/" + MetaModuleFactory.getApplication())?originalURI.substring(idx):originalURI;
			}
			else {
				forwardURI = "/";
			}
		}
		if (!Is.emptyString(originalParameters)) forwardURI = forwardURI + "?" + originalParameters.replace("__AMP__", "&"); 
		forwardURI = SignInHelper.refineForwardURI(getRequest(), forwardURI); 
	}

	public String getForwardURI() {
		return forwardURI;
	}

	public boolean inNewWindow() {
		return false;
	}

}

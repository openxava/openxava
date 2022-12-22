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
		System.out.println("[ForwardToOriginalURIBaseAction.forwardToOriginalURI] ParameterMap=" + getRequest().getParameterMap().keySet()); // tmr
		String originalURI = getRequest().getParameter("originalURI");
		String originalParameters = getRequest().getParameter("originalParameters"); // tmr
		System.out.println("[ForwardToOriginalURIBaseAction.forwardToOriginalURI] originalParameters=" + originalParameters); // tmr
		if (originalURI == null) {
			forwardURI = "/";
			System.out.println("[ForwardToOriginalURIBaseAction.forwardToOriginalURI] A"); // tmr
		}
		else {
			int idx = originalURI.indexOf("/", 1);			
			if (!originalURI.endsWith("/SignIn") && idx > 0 && idx < originalURI.length()) {
				forwardURI = originalURI.startsWith("/" + MetaModuleFactory.getApplication())?originalURI.substring(idx):originalURI;
				System.out.println("[ForwardToOriginalURIBaseAction.forwardToOriginalURI] B1"); // tmr
			}
			else {
				forwardURI = "/";
				System.out.println("[ForwardToOriginalURIBaseAction.forwardToOriginalURI] B2"); // tmr
			}
		}
		System.out.println("[ForwardToOriginalURIBaseAction.forwardToOriginalURI] forwardURI.1=" + forwardURI); // tmr
		// TMR ME QUEDÉ POR AQUÍ: ESTO FUNCIONA, PERO TENGO QUE VER IMPLICACIONES DE SEGURIDAD. ¿QUE HAY DE LOS SERVLETS?
		// tmr addPermalinkParameters();
		if (!Is.emptyString(originalParameters)) forwardURI = forwardURI + "?" + originalParameters.replace("__AMP__", "&"); // tmr
		System.out.println("[ForwardToOriginalURIBaseAction.forwardToOriginalURI] forwardURI.2=" + forwardURI); // tmr
		forwardURI = SignInHelper.refineForwardURI(getRequest(), forwardURI); 
		System.out.println("[ForwardToOriginalURIBaseAction.forwardToOriginalURI] forwardURI.3=" + forwardURI); // tmr
	}
	
	private void addPermalinkParameters() { 
		String detail = getRequest().getParameter("detail");
		if (!Is.emptyString(detail)) {
			forwardURI = forwardURI + "?detail=" + detail;  
		}
		else {
			String action = getRequest().getParameter("action");
			if (!Is.emptyString(action)) {
				forwardURI = forwardURI + "?action=" + action;  
			}			
		}
	}

	public String getForwardURI() {
		return forwardURI;
	}

	public boolean inNewWindow() {
		return false;
	}

}

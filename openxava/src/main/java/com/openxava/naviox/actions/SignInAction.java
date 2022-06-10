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

import org.openxava.util.*;

import com.openxava.naviox.impl.*;

/**
 * 
 * @author Javier Paniza 
 */
public class SignInAction extends ForwardToOriginalURIBaseAction {
	
	public void execute() throws Exception {		
		SignInHelper.initRequest(getRequest(), getView()); 
		if (getErrors().contains()) return; 
		String userName = getView().getValueString("user");
		String password = getView().getValueString("password");
		if (Is.emptyString(userName, password)) { 
			addError("unauthorized_user"); 
			return;
		}		
		if (!SignInHelper.isAuthorized(getRequest(), userName, password, getErrors())) { 
			return;									
		}		
		SignInHelper.signIn(getRequest(), userName); 
		getView().reset();
		getContext().resetAllModulesExceptCurrent(getRequest()); 
		forwardToOriginalURI();
	}
	
}

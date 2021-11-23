package com.openxava.naviox.actions;

import org.openxava.util.*;
import com.openxava.naviox.impl.*;

/**
 * 
 * @author Javier Paniza 
 */
public class SignInAction extends ForwardToOriginalURIBaseAction {
	
	public void execute() throws Exception {		
		SignInHelper.init(getRequest(), getView()); 
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

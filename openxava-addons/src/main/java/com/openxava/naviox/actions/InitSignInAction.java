package com.openxava.naviox.actions;

import org.openxava.actions.*;

import com.openxava.naviox.impl.*;

/**
 * @since 7.0
 * @author Javier Paniza
 */
public class InitSignInAction extends ViewBaseAction {  

	public void execute() throws Exception {
		String actionToAdd = SignInHelper.init(getRequest(), getView());
		if (actionToAdd != null) addActions(actionToAdd);
	}

}

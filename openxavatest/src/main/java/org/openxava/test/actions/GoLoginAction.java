package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza 
 */
public class GoLoginAction extends ViewBaseAction { 
	
	public void execute() throws Exception {
		showDialog();
		getView().setModelName("Login"); 
		getView().reset(); 
		setControllers("BlogLogin");
		removeActions("BlogLogin.notWanted");
	}

}

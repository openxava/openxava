package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza 
 */

public class LoginAction extends ViewBaseAction {

	public void execute() throws Exception {
		String user = getView().getValueString("user");
		String password = getView().getValueString("password");		
		if (user.equals("JAVI") && password.equals("x8Hjk37mm")) addMessage("ok");
		else addError("error");
	}
	
}

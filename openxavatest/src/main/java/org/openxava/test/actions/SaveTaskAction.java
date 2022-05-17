package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class SaveTaskAction extends SaveAction {
		
	public void execute() throws Exception {		
		super.execute();
		addMessage("given_name_is", "'" + Users.getCurrentUserInfo().getGivenName() + "'"); 
	}

}

package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */
public class OnChangeVoidAction extends OnChangePropertyBaseAction {
	
	public void execute() throws Exception {
		// Do nothing... if only for test a case		
		addMessage("on_change_void_executed");
	}

}

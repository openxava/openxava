package org.openxava.test.actions;

import org.openxava.actions.*;

public class OnChangeVoidCalendarAction extends OnChangePropertyBaseAction {
	
	public void execute() throws Exception {
		// Do nothing... if only for test a case		
		addMessage("Calendar changed");
	}

}

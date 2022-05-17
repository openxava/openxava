package org.openxava.test.actions;

import org.openxava.actions.*;

public class OnChangeAddMessageAction extends OnChangePropertyBaseAction {
	
	public void execute() throws Exception {
		addMessage("property_changed");
	}

}

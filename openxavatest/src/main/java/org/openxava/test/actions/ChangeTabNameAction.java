package org.openxava.test.actions;

import org.openxava.actions.*;

public class ChangeTabNameAction extends TabBaseAction {

	@Override
	public void execute() throws Exception {
		if (getTab().getTabName().equals("OnlyName")) {
			getTab().setTabName("");
		} else {
			getTab().setTabName("OnlyName");
		}
		
	}
	
}

package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class OnChangeSubfamilySearchAction extends OnChangeSearchAction {
	
	public void execute() throws Exception {
		if (getView().getValueInt("number") == 0) {
			getView().setValue("number", new Integer("1"));
		}
		super.execute();
	}
	
}

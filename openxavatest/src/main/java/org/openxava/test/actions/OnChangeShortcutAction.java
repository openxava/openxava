package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class OnChangeShortcutAction extends OnChangePropertyBaseAction{

	public void execute() throws Exception {
		if ("NR".equals(getNewValue())) {
			getView().setValue("remarks", "No remarks");
		}
		else if ("DY".equals(getNewValue())) {
			getView().setValue("remarks", "Delayed");
		} 
	}


}

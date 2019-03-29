package org.openxava.actions;

import org.apache.commons.logging.*;

/**
 * @author Javier Paniza
 */

public class GoNextPageAction extends TabBaseAction {
	private static Log log = LogFactory.getLog(GoNextPageAction.class);
	
	public void execute() throws Exception {
		getTab().pageForward();
		getTab().setNotResetNextTime(true);
		
		// getTab().setAllSelected(new int[0]);
	}

}

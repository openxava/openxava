package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class ShowOutOfPrintBooksOnlyAction extends TabBaseAction {

	public void execute() throws Exception {
		getTab().setConditionValue("outOfPrint", true);
	}

}

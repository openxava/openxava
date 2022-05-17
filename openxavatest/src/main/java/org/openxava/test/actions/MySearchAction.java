package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */

public class MySearchAction extends ReferenceSearchAction {
		
	public void execute() throws Exception {
		super.execute();
		getTab().setTabName("Other"); 
		getTab().setBaseCondition("${number} < 3");
	}

}

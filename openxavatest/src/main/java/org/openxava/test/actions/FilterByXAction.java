package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class FilterByXAction extends TabBaseAction {

	public void execute() throws Exception {
		getTab().filterByContentInAnyProperty("X");
	}

}

package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class FilterByDescriptionAction extends TabBaseAction {

	public void execute() throws Exception {
		getTab().setBaseCondition("${description} like 'FOR TEST%'");
	}

}

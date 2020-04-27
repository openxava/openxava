package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * tmp
 * 
 * @author Javier Paniza
 */
public class FilterBySellerOneAction extends TabBaseAction {

	public void execute() throws Exception {
		addMessage("Fritering 2"); // tmp
		getTab().setConditionValue("seller.number", 1);
	}

}

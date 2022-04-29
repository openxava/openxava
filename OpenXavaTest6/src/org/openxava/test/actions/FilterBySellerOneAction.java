package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class FilterBySellerOneAction extends TabBaseAction {

	public void execute() throws Exception {
		getTab().setConditionValue("seller.name", 1);
	}

}

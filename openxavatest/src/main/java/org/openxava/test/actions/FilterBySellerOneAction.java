package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class FilterBySellerOneAction extends TabBaseAction {

	public void execute() throws Exception {
		// TMR getTab().setConditionValue("seller.name", 1);
		getTab().setConditionValue("seller.name", "MANUEL CHAVARRI"); // TMR EN MIGRATION
	}

}

package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class GoAssignSellerToCustomersAction extends ViewBaseAction {

	public void execute() throws Exception {
		showDialog();
		getView().setModelName("Customer");
		getView().setViewName("OnlySeller");
		setControllers("AssignSellerToCustomers");
	}
	
}

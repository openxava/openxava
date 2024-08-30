package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Chungyen Tsai
 */
public class ShowCustomerAction extends ViewBaseAction {
	
	public void execute() throws Exception {
		showDialog();
		getView().setModelName("Customer");
		getView().setViewName("SellerAsAggregate");
		getView().setValue("number", 1);
		setControllers("Dialog", "ShowSellerDialog"); 
		getView().refresh();		
	}

}

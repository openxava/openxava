package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza 
 */
public class ShowGoCustomerInvoicesDialogAction extends ViewBaseAction {
	
	public void execute() throws Exception {
		showDialog();
		setControllers("GoCustomerInvoices");
		getView().setModelName("Customer");
		getView().setViewName("Intermediate");
	}

}

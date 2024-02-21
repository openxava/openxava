package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class OnChangeVisitCustomerAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {
		int km = getView().getValueInt("km");
		String customerName = getView().getValueString("customer.name");		
		getView().setValue("description", "KM: " + km + ", CUSTOMER: " + getNewValue() + " " + customerName);
	}

}

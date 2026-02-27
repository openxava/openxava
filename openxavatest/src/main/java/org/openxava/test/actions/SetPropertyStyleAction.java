package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */
public class SetPropertyStyleAction extends ViewBaseAction {

	public void execute() throws Exception {
		getView().setStyle("date", "red");
		getView().setStyle("customerDiscount", "blue");
		getView().setStyle("customer.name", "green");
		getView().setStyle("customer.address.street", "orange");
	}

}

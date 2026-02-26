package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */
public class SetPropertyStyleAction extends ViewBaseAction {

	public void execute() throws Exception {
		getView().setStyle("date", "ox-color-red");
		getView().setStyle("customerDiscount", "ox-color-blue");
		getView().setStyle("customer.name", "ox-color-green");
		getView().setStyle("customer.address.street", "ox-color-orange");
	}

}

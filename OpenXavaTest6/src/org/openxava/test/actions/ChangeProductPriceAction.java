package org.openxava.test.actions;

import org.openxava.actions.*;

public class ChangeProductPriceAction extends ViewBaseAction implements IChangeControllersAction {

	public void execute() throws Exception {
		getView().setEditable(false); 
	}

	public String[] getNextControllers() throws Exception {
		return new String [] {
			"ChangeProductsPrice"
		};
	}

}

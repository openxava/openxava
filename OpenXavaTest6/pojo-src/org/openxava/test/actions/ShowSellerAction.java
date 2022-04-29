package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza 
 */
public class ShowSellerAction extends ViewBaseAction {
	
	public void execute() throws Exception {
		int number = (Integer) getView().getValues().get("number");
		showDialog();
		getView().setModelName("Seller");
		getView().setViewName("Complete");
		getView().setValue("number", number);
		setControllers("ModifySeller", "Dialog"); 
		getView().refresh();		
	}

}

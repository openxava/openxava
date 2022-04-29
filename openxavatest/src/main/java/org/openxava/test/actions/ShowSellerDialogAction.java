package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class ShowSellerDialogAction extends ViewBaseAction {

	public void execute() throws Exception {
		// Actually we don't show the seller data, just the dialog, for testing a case
		showDialog();
		getView().setModelName("Seller");
		getView().setViewName("SimpleLevelFirst");
	}

}

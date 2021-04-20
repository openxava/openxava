package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * tmp
 * 
 * @author Javier Paniza
 */
public class SetTransientSellerRefreshingInSubviewAction extends ViewBaseAction {

	public void execute() throws Exception {
		getView().getSubview("transientSeller").setValue("number", 2); // Using getSubview() to test a case
		getView().getSubview("transientSeller").refresh();
	}

}

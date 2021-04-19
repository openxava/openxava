package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * tmp
 * 
 * @author Javier Paniza
 */
public class SetTransientSellerRefreshingAction extends ViewBaseAction {

	public void execute() throws Exception {
		getView().setValue("transientSeller.number", 3); // TMP PROBAR CONTRA LA SUBVISTA
		getView().getSubview("transientSeller").refresh();
	}

}

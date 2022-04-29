package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.test.model.*;



/**
 * @author Javier Paniza
 */

public class OnChangeCustomerNameAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {
		String value = (String) getNewValue();
		if (value == null) return;
		if (value.startsWith("Javi")) {
			getView().setValue("type", Customer.Type.STEADY);
		}	
	}

}

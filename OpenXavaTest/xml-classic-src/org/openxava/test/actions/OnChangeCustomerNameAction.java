package org.openxava.test.actions;

import org.openxava.actions.*;



/**
 * @author Javier Paniza
 */

public class OnChangeCustomerNameAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {			
		String value = (String) getNewValue();
		if (value == null) return;
		if (value.startsWith("Javi")) {
			getView().setValue("type", new Integer(2));
		}	
	}

}

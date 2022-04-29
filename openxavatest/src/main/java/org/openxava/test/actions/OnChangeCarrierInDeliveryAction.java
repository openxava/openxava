package org.openxava.test.actions;

import org.openxava.actions.*;


/**
 * @author Javier Paniza
 */
public class OnChangeCarrierInDeliveryAction extends OnChangePropertyBaseAction {
	
	public void execute() throws Exception {
		if (getNewValue() == null) return;
		getView().setValue("remarks", "The carrier is " + getNewValue());
		addMessage("carrier_changed");		
	}

}

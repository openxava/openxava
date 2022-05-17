package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class GoAddDeliveryFromInvoiceAction extends GoAddElementsToCollectionAction {
	
	public void execute() throws Exception {
		super.execute();
		addMessage("choose_delivery_to_invoice"); 
	}

}

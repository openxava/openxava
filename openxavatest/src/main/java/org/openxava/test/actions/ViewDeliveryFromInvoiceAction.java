package org.openxava.test.actions;

import org.openxava.actions.*;

public class ViewDeliveryFromInvoiceAction extends EditElementInCollectionAction {

	public void execute() throws Exception {
		super.execute();
		addMessage("delivery_displayed"); 
	}
	
}

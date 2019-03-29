package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.view.*;


/**
 * @author Javier Paniza
 */
public class AddViewDeliveryInInvoiceAction extends ViewBaseAction {
		
	public void execute() throws Exception {
		View subview = getView().getSubview("deliveries");
		subview.setHidden("type", false);		
	}

}

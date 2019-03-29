package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.util.*;


/**
 * @author Javier Paniza
 */
public class OnChangeInvoiceNumberInDeliveryAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {			
		Number n = (Number) getNewValue();
		int number = n == null?0:n.intValue();
		
		if (!Is.emptyString(getView().getValueString("remarks"))) return;
		
		if (number == 1) {			
			getView().setValue("remarks", "First invoice of year");
		}
		else  {
			String remarks = (String) getView().getValue("remarks");
			if (remarks == null) remarks = "";
			getView().setValue("remarks", remarks + "No remarks");
		}
	}

}

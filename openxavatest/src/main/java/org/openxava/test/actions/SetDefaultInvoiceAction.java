package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.view.*;

/**
 * 
 * @author Javier Paniza
 */

public class SetDefaultInvoiceAction extends ViewBaseAction {

	public void execute() throws Exception {
		View invoiceView = getView().getSubview("invoice");
		invoiceView.setValue("year", new Integer(2002));
		invoiceView.setValue("number", new Integer(1));
		invoiceView.findObject();
	}

}

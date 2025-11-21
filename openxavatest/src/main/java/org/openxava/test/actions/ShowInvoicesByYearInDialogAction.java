package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.test.model.*;

/**
 * tmr
 * 
 * @author Javier Paniza
 */
public class ShowInvoicesByYearInDialogAction extends ViewBaseAction {

	public void execute() throws Exception {
		showDialog();
		getView().setModelName(InvoicesByYear.class.getSimpleName());
		getView().setViewName("ForDialog");
	}

}

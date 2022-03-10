package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.test.model.*;

/**
 * tmr ¿Quitar?
 * @author Javier Paniza
 */
public class InvoicesByYearInitAction extends ViewBaseAction {

	public void execute() throws Exception {
		System.out.println("[InvoicesByYearInitAction.execute] "); // tmp
		getView().setModel(new InvoicesByYear()); // tmr
	}

}

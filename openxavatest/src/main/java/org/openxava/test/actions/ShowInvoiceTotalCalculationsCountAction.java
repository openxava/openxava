package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.test.model.*;

/**
 * tmr 
 * 
 * @author Javier Paniza
 */
public class ShowInvoiceTotalCalculationsCountAction extends BaseAction {

	public void execute() throws Exception {
		addMessage("totalCalculationsCount=" + Invoice.getTotalCalculationsCount());
	}

}

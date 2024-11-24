package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.test.model.*;

/**
 * 
 * @author Javier Paniza
 */
public class ResetInvoiceTotalCalculationsCountAction extends BaseAction {

	public void execute() throws Exception {
		Invoice.resetTotalCalculationsCount();
	}

}

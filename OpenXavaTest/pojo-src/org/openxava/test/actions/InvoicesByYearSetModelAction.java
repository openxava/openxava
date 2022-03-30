package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.test.model.*;

/**
 *  
 * @author Javier Paniza
 */
public class InvoicesByYearSetModelAction extends ViewBaseAction {

	public void execute() throws Exception {
		getView().setModel(new InvoicesByYear()); 
	}

}

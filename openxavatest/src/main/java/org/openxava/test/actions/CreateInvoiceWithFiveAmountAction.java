package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.util.*;



/**
 * 
 * @author Javier Paniza
 */
public class CreateInvoiceWithFiveAmountAction extends ViewBaseAction {

	public void execute() throws Exception {
		Map key = getView().getKeyValues();
		Map values = getView().getValues();
		values.put("amount", 5);
		MapFacade.create(getModelName(), values);
	}

}

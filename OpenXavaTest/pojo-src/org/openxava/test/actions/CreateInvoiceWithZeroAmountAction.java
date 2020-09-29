package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.util.*;



/**
 * tmp 
 * @author Javier Paniza
 */
public class CreateInvoiceWithZeroAmountAction extends ViewBaseAction {

	public void execute() throws Exception {
		// TMP ME QUEDÉ POR AQUÍ. TRATANDO DE REPRODUCIR EL BUG. CREO QUE LO CONSEGUÍ
		Map key = getView().getKeyValues();
		Map values = getView().getValues();
		values.put("amount", 0);
		System.out.println("[CreateInvoiceWithZeroAmountAction.execute] >"); // tmp
		MapFacade.create(getModelName(), values);
		System.out.println("[CreateInvoiceWithZeroAmountAction.execute] <"); // tmp
	}

}

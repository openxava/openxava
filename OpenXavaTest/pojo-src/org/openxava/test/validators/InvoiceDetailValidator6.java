package org.openxava.test.validators;

import org.openxava.test.model.*;
import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * @author Javier Paniza
 */

public class InvoiceDetailValidator6 implements IValidator {
	
	private Invoice6 invoice;

	public void validate(Messages errors) throws Exception {
		if (invoice == null) return; 
		if (invoice.getYear() < 2010) {
			errors.add("detail_not_added_invoice_too_old"); 
		}		
	}
	

	public Invoice6 getInvoice() {
		return invoice;
	}
	public void setInvoice(Invoice6 invoice) {
		this.invoice = invoice;
	}
	
}

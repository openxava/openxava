package org.openxava.test.validators;

import java.math.*;

import org.openxava.test.model.*;
import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * @author Javier Paniza
 */

public class InvoiceDetailValidator6 implements IValidator {
	
	private Invoice6 invoice;

	public void validate(Messages errors) throws Exception {
		System.out.println("[InvoiceDetailValidator6.validate] "); // tmp
		if (invoice == null) return; // tmp QUITAR, SOLO ES PARA PODER BORRARLA MIENTRAS TESTEAMOS
		if (invoice.getYear() < 2010) {
			errors.add("Detail not added: Invoice too old"); // tmp i18n
		}		
	}
	

	public Invoice6 getInvoice() {
		return invoice;
	}
	public void setInvoice(Invoice6 invoice) {
		this.invoice = invoice;
	}
	
}

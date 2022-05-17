package org.openxava.test.validators;

import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * @author Javier Paniza
 */
public class DeliveryValidator implements IValidator {

	private Object invoice;
	
	public void validate(Messages errors) throws Exception {
		// This validator is only for verify a error to combine
		// validators and key references. Hence is empty
	}

	public Object getInvoice() {
		return invoice;
	}
	public void setInvoice(Object factura) {
		this.invoice = factura;
	}
	
}

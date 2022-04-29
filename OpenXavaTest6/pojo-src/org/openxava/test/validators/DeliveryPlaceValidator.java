package org.openxava.test.validators;

import org.openxava.test.model.*;
import org.openxava.util.Messages;
import org.openxava.validators.IValidator;

/**
 * Create on 16/08/2006 (12:07:01)
 * @autor Ana Andr�s
 */

public class DeliveryPlaceValidator implements IValidator {
	
	private Customer customer;
	
	public void validate(Messages errors) throws Exception {		
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

}

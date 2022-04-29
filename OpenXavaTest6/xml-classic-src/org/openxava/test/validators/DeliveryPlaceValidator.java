package org.openxava.test.validators;

import org.openxava.test.model.*;
import org.openxava.util.Messages;
import org.openxava.validators.IValidator;

/**
 * Create on 16/08/2006 (12:07:01)
 * @autor Ana Andrés
 */

public class DeliveryPlaceValidator implements IValidator {
	
	private ICustomer customer;
	
	public void validate(Messages errors) throws Exception {
	
	}

	public ICustomer getCustomer() {
		return customer;
	}

	public void setCustomer(ICustomer customer) {
		this.customer = customer;
	}

}

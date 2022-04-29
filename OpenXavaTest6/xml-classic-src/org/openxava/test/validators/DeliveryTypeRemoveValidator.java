package org.openxava.test.validators;

import org.openxava.test.model.*;
import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * @author Javier Paniza
 */
public class DeliveryTypeRemoveValidator implements IRemoveValidator {
	
	private IDeliveryType deliveryType;
	private int number; // We us this (instado of obtain it from deliveryType) for testing <set /> for simple properties

	public void setEntity(Object entity) throws Exception {
		this.deliveryType = (IDeliveryType) entity; 		
	}

	public void validate(Messages errors) throws Exception {
		if 	(!deliveryType.getDeliveries().isEmpty()) {
			errors.add("not_remove_delivery_type_if_in_deliveries", new Integer(getNumber()));	
		}
	}

	public int getNumber() {
		return number; 
	}

	public void setNumber(int number) {
		this.number = number;
	}

}

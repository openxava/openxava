package org.openxava.test.validators;

import org.openxava.test.model.*;
import org.openxava.util.*;
import org.openxava.validators.*;

/**
 * @author Javier Paniza
 */
public class DeliveryTypeRemoveValidator implements IRemoveValidator {
	
	private DeliveryType deliveryType;
	private int number; // We use this (instead of obtaining it from deliveryType) for testing @PropertyValue for simple properties

	public void setEntity(Object entity) throws Exception {
		this.deliveryType = (DeliveryType) entity; 		
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

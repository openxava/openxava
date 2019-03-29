package org.openxava.test.calculators;

import java.rmi.*;

import org.openxava.calculators.*;
import org.openxava.test.model.*;

/**
 * @author Javier Paniza
 */
public class DeliveryTypePostcreateCalculator implements IModelCalculator {
	
	private IDeliveryType deliveryType;
	private String suffix;
	private int number; // It is not used, only for verifying that works properties with 'from'

	public Object calculate() throws Exception {
		deliveryType.setDescription(deliveryType.getDescription() + " " + suffix);
		return null;
	}

	public void setModel(Object entity) throws RemoteException {
		deliveryType = (IDeliveryType) entity;		
	}

	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	
}

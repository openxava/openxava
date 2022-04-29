package org.openxava.test.calculators;

import java.math.*;
import java.rmi.*;

import org.openxava.calculators.*;
import org.openxava.test.model.*;

/**
 * @author Javier Paniza
 */
public class IncreasePriceCalculator implements IModelCalculator {
	
	private IProduct product;

	public Object calculate() throws Exception {
		product.setUnitPrice(product.getUnitPrice().multiply(new BigDecimal("1.02")).setScale(2));
		return null;				
	}

	public void setModel(Object entity) throws RemoteException {
		this.product = (IProduct) entity;
	}

}

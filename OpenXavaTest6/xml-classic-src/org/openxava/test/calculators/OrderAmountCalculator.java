package org.openxava.test.calculators;

import java.math.*;
import java.rmi.*;
import java.util.*;

import org.openxava.calculators.*;
import org.openxava.test.model.*;

/**
 * 
 * @author Javier Paniza
 */

public class OrderAmountCalculator implements IModelCalculator {
	
	private IOrder order;

	public Object calculate() throws Exception {
		BigDecimal result = new BigDecimal("0.00");
		Collection details = order.getDetails();
		if (details != null) {
			for (Iterator it = details.iterator(); it.hasNext(); ) {
				IOrderDetail detail = (IOrderDetail) it.next();
				result = result.add(detail.getAmount());
			}
		}
		return result;
	}
	
	public void setModel(Object model) throws RemoteException {
		this.order = (IOrder) model;
	}

}

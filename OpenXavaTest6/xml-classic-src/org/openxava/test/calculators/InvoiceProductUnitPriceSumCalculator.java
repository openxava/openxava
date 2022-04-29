package org.openxava.test.calculators;

import org.openxava.test.model.*;

import java.math.*;
import java.rmi.*;
import java.util.*;

import org.openxava.calculators.*;

/**
 * 
 * @author Javier Paniza
 */
public class InvoiceProductUnitPriceSumCalculator implements IModelCalculator {

	private IInvoice invoice;

	public Object calculate() throws Exception {
		BigDecimal result = BigDecimal.ZERO;		
		for (InvoiceDetail detail: (Collection<InvoiceDetail>) invoice.getDetails()) {			
			result = result.add(detail.getProduct().getUnitPrice());
		}		
		return result;
	}
	
	public void setModel(Object model) throws RemoteException {
		invoice = (IInvoice) model;		
	}
	

}

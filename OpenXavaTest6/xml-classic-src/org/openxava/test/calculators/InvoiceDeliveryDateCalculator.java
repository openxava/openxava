package org.openxava.test.calculators;

import org.apache.commons.lang.*;
import org.openxava.test.model.*;

import java.rmi.*;
import java.util.*;

import org.openxava.calculators.*;

/**
 * 
 * @author Javier Paniza
 */
public class InvoiceDeliveryDateCalculator implements IModelCalculator {

	private IInvoice invoice;

	public Object calculate() throws Exception {
		Date result = null;		
		for (InvoiceDetail detail: (Collection<InvoiceDetail>) invoice.getDetails()) {
			result = (Date) ObjectUtils.min(result, detail.getDeliveryDate());
		}		
		return result;
	}
	
	public void setModel(Object model) throws RemoteException {
		invoice = (IInvoice) model;		
	}
	

}

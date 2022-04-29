package org.openxava.test.calculators;

import java.math.*;
import java.rmi.*;
import java.util.*;

import javax.rmi.*;

import org.openxava.calculators.*;
import org.openxava.test.model.*;

/**
 * @author Javier Paniza
 */

public class AmountsSumCalculator implements IModelCalculator {
	
	private IInvoice invoice;

	public Object calculate() throws Exception {
		Iterator itDetails = invoice.getDetails().iterator();
		BigDecimal result = new BigDecimal("0");
		while (itDetails.hasNext()) {
			IInvoiceDetail detail = (IInvoiceDetail) PortableRemoteObject.narrow(itDetails.next(), IInvoiceDetail.class);
			result = result.add(detail.getAmount());
		}		
		return result;
	}

	public void setModel(Object entity) throws RemoteException {		
		invoice = (IInvoice) PortableRemoteObject.narrow(entity, IInvoice.class);		
	}

}

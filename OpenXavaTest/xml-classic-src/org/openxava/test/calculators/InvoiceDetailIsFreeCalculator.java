package org.openxava.test.calculators;

import java.math.*;
import java.rmi.*;
import org.openxava.calculators.*;
import org.openxava.test.model.*;

/**
 * 
 * @author Javier Paniza
 */

public class InvoiceDetailIsFreeCalculator implements IModelCalculator {

	private IInvoiceDetail detail;

	public Object calculate() throws Exception {
		return new Boolean(detail.getAmount().compareTo(new BigDecimal("0")) <= 0);
	}
	
	public void setModel(Object model) throws RemoteException {
		this.detail = (IInvoiceDetail) model;
	}


}

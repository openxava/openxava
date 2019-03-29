package org.openxava.test.calculators;

import java.rmi.*;

import org.openxava.calculators.*;
import org.openxava.test.model.*;

/**
 * @author Javier Paniza
 */
public class DetailPostremoveCalculator implements IModelCalculator {
	
	private IInvoice invoice;

	public Object calculate() throws Exception {		
		invoice.setComment(invoice.getComment() + "DETAIL DELETED");
		return null;
	}

	public void setModel(Object entity) throws RemoteException {
		this.invoice = (IInvoice) entity;
	}

}

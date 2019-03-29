package org.openxava.test.calculators;

import java.rmi.*;

import org.openxava.calculators.*;
import org.openxava.hibernate.XHibernate;
import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */
public class InvoiceDetailsCalculator implements IModelCalculator {

	private IInvoice invoice;

	public Object calculate() throws Exception {
		return invoice.getDetails();
	}

	public void setModel(Object entity) throws RemoteException {
		invoice = (IInvoice) entity;		
	}

}
 
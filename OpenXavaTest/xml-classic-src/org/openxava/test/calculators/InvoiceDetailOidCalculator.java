package org.openxava.test.calculators;

import org.openxava.calculators.*;
import org.openxava.test.model.*;


/**
 * The container is injected using setContainer method of IAggregateOidCalculator.
 * 
 * @author Javier Paniza
 */
public class InvoiceDetailOidCalculator implements IAggregateOidCalculator {
	
	private IInvoice invoice;
	private int counter;

	public void setContainer(Object containerKey) {
		// In the case of POJO container key is the container POJO itself,
		// in the case of EJB the container key is the EJB key
		invoice = (IInvoice) containerKey;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public Object calculate() throws Exception {
		return invoice.getYear() + ":" + invoice.getNumber() + ":" + counter;
	}

}

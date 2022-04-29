package org.openxava.test.calculators;

import org.openxava.calculators.*;


/**
 * The container data is injected by means of simple property with 'from'.
 * 
 * @author Javier Paniza
 */
public class InvoiceDetail2OidCalculator implements ICalculator {

	private static int counter = 0;
	private int invoiceYear;
	private int invoiceNumber;

	public Object calculate() throws Exception {
		if (invoiceYear == 0 || invoiceNumber == 0) {
			throw new IllegalStateException("It's requerid to fill 'invoiceYear' and 'invoiceNumber' properties of InvoiceDetail2OidCalculator before call to execute()");
		}
		return invoiceYear + ":" + invoiceNumber + ":" + counter++;
	}

	public int getInvoiceYear() {
		return invoiceYear;
	}

	public void setInvoiceYear(int invoiceYear) {
		this.invoiceYear = invoiceYear;
	}

	public int getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(int invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

}

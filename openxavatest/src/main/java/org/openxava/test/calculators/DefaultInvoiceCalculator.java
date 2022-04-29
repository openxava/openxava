package org.openxava.test.calculators;

import org.openxava.calculators.*;
import org.openxava.test.model.*;

/**
 * 
 * @author Javier Paniza 
 */
public class DefaultInvoiceCalculator implements ICalculator { 
	
	public Object calculate() throws Exception {
		Invoice invoice = new Invoice();
		invoice.setYear(2002);
		invoice.setNumber(1);
		return invoice;
	}

}

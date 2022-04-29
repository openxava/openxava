package org.openxava.test.calculators;

import java.math.*;

import org.openxava.calculators.*;

/**
 * @author Javier Paniza
 */
public class CustomerTypeInvoiceDiscountCalculator implements ICalculator {

	private int type;

	/**
	 * @see org.openxava.calculators.ICalculator#calculate()
	 */
	public Object calculate() throws Exception {		
		if (type == 2) return new BigDecimal("20.00");
		if (type == 3) return new BigDecimal("30.00");
		return new BigDecimal("00.00");
	}

	public int getType() {
		return type;
	}

	public void setType(int i) {
		type = i;
	}

}

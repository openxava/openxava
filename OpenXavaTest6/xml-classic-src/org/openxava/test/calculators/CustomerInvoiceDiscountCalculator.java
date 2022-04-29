package org.openxava.test.calculators;

import java.math.*;

import org.openxava.calculators.*;

/**
 * @author Javier Paniza
 */
public class CustomerInvoiceDiscountCalculator implements ICalculator {

	private int number;
	private boolean paid;

	public Object calculate() throws Exception {
		if (paid) return new BigDecimal("77");
		if (number == 1) return new BigDecimal("11.50");
		if (number == 2) return new BigDecimal("22.75");
		return new BigDecimal("0.25");
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {		
		this.number = number;
	}

	public boolean isPaid() {
		return paid;
	}
	public void setPaid(boolean paid) {
		this.paid = paid;
	}
}

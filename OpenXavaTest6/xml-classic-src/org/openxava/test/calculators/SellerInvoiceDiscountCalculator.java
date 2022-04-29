package org.openxava.test.calculators;

import java.math.*;

import org.openxava.calculators.*;

/**
 * @author Javier Paniza
 */
public class SellerInvoiceDiscountCalculator implements ICalculator {
	
	final private static BigDecimal ZERO = new BigDecimal("0.00");
	final private static BigDecimal DISCOUNT = new BigDecimal("20.00");
	
	private int sellerNumber;

	public Object calculate() throws Exception {
		return sellerNumber == 1?DISCOUNT:ZERO;		
	}

	public int getSellerNumber() {
		return sellerNumber;
	}
	public void setSellerNumber(int sellerNumber) {
		this.sellerNumber = sellerNumber;
	}
}

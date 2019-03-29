package org.openxava.test.calculators;  

import org.openxava.calculators.*;
import org.openxava.test.model.*;

import static org.openxava.jpa.XPersistence.*; 

/**
 * 
 * @author Javier Paniza 
 */
public class UnitPriceCalculator implements ICalculator {
	
	private static long times = 0;
	
	private long productNumber;  

	public Object calculate() throws Exception {
		if (++times > 1) return -1l; // To test that calculator is not executed twice in QuoteTest.testDependentDefaultValueCalculatorInElementCollection()
		Product	product = getManager().find(Product.class, productNumber);  
		return product.getUnitPrice();
	}

	public void setProductNumber(long productNumber) { 
		this.productNumber = productNumber;
	}

	public long getProductNumber() {
		return productNumber;
	}

	public static void reset() {
		times = 0;		
	}

}
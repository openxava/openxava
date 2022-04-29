package org.openxava.test.calculators;

import java.math.*;
import java.util.*;

import org.openxava.calculators.*;
import org.openxava.model.*;
import org.openxava.test.model.*;

/**
 * 
 * @author Javier Paniza
 */

public class OrderDetailAmountCalculator implements ICalculator {
	
	private int quantity; 
	private long productNumber;

	public Object calculate() throws Exception {
		return new BigDecimal(quantity).multiply(getProduct().getUnitPrice());
	}
	
	public IProduct getProduct() throws Exception {
		// Using MapFacade in order to work with both EJB2 CMP and Hibernate
		Map key = new HashMap();
		key.put("number", new Long(productNumber));
		return (IProduct) MapFacade.findEntity("Product", key);
	}
	
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public long getProductNumber() {
		return productNumber;
	}

	public void setProductNumber(long productNumber) {
		this.productNumber = productNumber;
	}
	
}

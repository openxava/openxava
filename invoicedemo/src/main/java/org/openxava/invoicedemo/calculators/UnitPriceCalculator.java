package org.openxava.invoicedemo.calculators;

import static org.openxava.jpa.XPersistence.getManager;

import org.openxava.calculators.*;
import org.openxava.invoicedemo.model.*;

public class UnitPriceCalculator implements ICalculator {
	
	private int productNumber;  

	public Object calculate() throws Exception {
		Product	product = getManager().find(Product.class, productNumber);  
		return product.getUnitPrice();  	
	}

	public void setProductNumber(int productNumber) { 
		this.productNumber = productNumber;
	}

	public int getProductNumber() {
		return productNumber;
	}

}

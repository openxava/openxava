package org.openxava.chattest.calculators;

import static org.openxava.jpa.XPersistence.getManager;

import org.openxava.calculators.*;

import org.openxava.chattest.model.*;

import lombok.*;

public class UnitPriceCalculator implements ICalculator {
	
	@Getter @Setter
	int number;  

	public Object calculate() throws Exception {
		return getManager().find(Product.class, number).getUnitPrice();  
	}

}

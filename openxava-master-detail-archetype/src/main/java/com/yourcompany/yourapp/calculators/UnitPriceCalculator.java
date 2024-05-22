package com.yourcompany.yourapp.calculators;

import static org.openxava.jpa.XPersistence.getManager;

import org.openxava.calculators.*;

import com.yourcompany.yourapp.model.*;

import lombok.*;

public class UnitPriceCalculator implements ICalculator {
	
	@Getter @Setter
	int number;  

	public Object calculate() throws Exception {
		return getManager().find(Item.class, number).getUnitPrice();  
	}

}

package org.openxava.test.calculators;

import java.math.*;

import org.openxava.calculators.*;

/**
 * @author Javier Paniza
 */
public class DefaultProductPriceCalculator implements ICalculator {
	
	private int familyNumber;

	public Object calculate() throws Exception {
		return new BigDecimal(Integer.toString(familyNumber*10));
	}

	public int getFamilyNumber() {
		return familyNumber;
	}

	public void setFamilyNumber(int familyNumber) {
		this.familyNumber = familyNumber;
	}

}

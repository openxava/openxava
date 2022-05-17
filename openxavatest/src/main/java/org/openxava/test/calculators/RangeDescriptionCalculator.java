package org.openxava.test.calculators;

import org.openxava.calculators.*;

/**
 * 
 * @author Javier Paniza
 */

public class RangeDescriptionCalculator implements ICalculator {
	
	private int subfamilyNumberFrom;
	private int subfamilyNumberTo;

	public Object calculate() throws Exception {
		return "FROM SUBFAMILY " + subfamilyNumberFrom + " TO SUBFAMILY " + subfamilyNumberTo;
	}

	public int getSubfamilyNumberFrom() {
		return subfamilyNumberFrom;
	}

	public void setSubfamilyNumberFrom(int subfamilyNumberFrom) {
		this.subfamilyNumberFrom = subfamilyNumberFrom;
	}

	public int getSubfamilyNumberTo() {
		return subfamilyNumberTo;
	}

	public void setSubfamilyNumberTo(int subfamilyNumberTo) {
		this.subfamilyNumberTo = subfamilyNumberTo;
	}

}

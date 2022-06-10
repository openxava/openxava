package org.openxava.calculators;





/**
 * @author Javier Paniza
 */
public class IntegerCalculator implements ICalculator {

	private int value;
	
	
	public Object calculate() throws Exception {
		return new Integer(value);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int i) {
		value = i;
	}

}

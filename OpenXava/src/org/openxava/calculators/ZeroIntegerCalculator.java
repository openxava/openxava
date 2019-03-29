package org.openxava.calculators;





/**
 * @author Javier Paniza
 */
public class ZeroIntegerCalculator implements ICalculator {
	
	private final static Integer ZERO = new Integer(0);
	

	public Object calculate() throws Exception {
		return ZERO;
	}

}

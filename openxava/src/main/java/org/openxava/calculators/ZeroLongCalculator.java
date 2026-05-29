package org.openxava.calculators;





/**
 * @author Javier Paniza
 */
public class ZeroLongCalculator implements ICalculator {
	
	private final static Long ZERO = Long.valueOf(0);
	

	public Object calculate() throws Exception {
		return ZERO;
	}

}

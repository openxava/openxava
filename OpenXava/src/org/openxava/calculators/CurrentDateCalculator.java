package org.openxava.calculators;






/**
 * @author Javier Paniza
 */
public class CurrentDateCalculator implements ICalculator {

	
	
	public Object calculate() throws Exception {
		return new java.util.Date();
	}

}

package org.openxava.calculators;



import org.openxava.util.*;


/**
 * @author Javier Paniza
 */
public class NullDateCalculator implements ICalculator {

	private final static java.util.Date NULL_DATE = Dates.create(1, 1, 1); 	
	

	/**
	 * @see org.openxava.calculators.ICalculator#calculate()
	 */
	public Object calculate() throws Exception {
		return NULL_DATE;
	}

}

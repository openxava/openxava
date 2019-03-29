package org.openxava.calculators;

import java.util.*;

/**
 * @author Javier Paniza
 */
public class CurrentYearCalculator implements ICalculator {
	
	public Object calculate() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new java.util.Date());
		return new Integer(cal.get(Calendar.YEAR));
	}

}

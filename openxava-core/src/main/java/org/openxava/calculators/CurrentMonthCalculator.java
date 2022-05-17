package org.openxava.calculators;

import java.util.*;

/**
 * @author Trifon Trifonov
 */
public class CurrentMonthCalculator implements ICalculator {
	
	private static final long serialVersionUID = 1546879332996096585L;

	public Object calculate() throws Exception {
		Calendar cal = Calendar.getInstance();
		cal.setTime( new java.util.Date() );
		return new Integer(cal.get(Calendar.MONTH) + 1);
	}

}
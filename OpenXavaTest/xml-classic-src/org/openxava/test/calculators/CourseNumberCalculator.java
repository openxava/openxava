package org.openxava.test.calculators;

import org.openxava.calculators.*;

/**
 * 
 * @author Javier Paniza
 */

public class CourseNumberCalculator implements ICalculator {

	public Object calculate() throws Exception {
		return new Integer((int) (System.currentTimeMillis() % 100000));		
	}

}

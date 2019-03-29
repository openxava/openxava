package org.openxava.test.calculators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.calculators.ICalculator;

/**
 * Create on 13/04/2010 (12:29:45)
 * @author Ana Andrés
 */
public class IsSlowCalculator implements ICalculator{
	private static Log log = LogFactory.getLog(IsSlowCalculator.class);
	
	private int mode;
	
	public Object calculate() throws Exception {
		return getMode() == 1;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}
}

package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.test.calculators.*;

/**
 * 
 * @author Javier Paniza 
 */

public class ResetUnitPriceCalculatorAction extends BaseAction {

	public void execute() throws Exception {
		UnitPriceCalculator.reset();	
	}
	
}

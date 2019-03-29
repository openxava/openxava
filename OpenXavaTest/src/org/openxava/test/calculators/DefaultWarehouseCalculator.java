package org.openxava.test.calculators;

import org.openxava.calculators.*;
import org.openxava.test.model.*;

/**
 * @author Javier Paniza
 */
public class DefaultWarehouseCalculator implements ICalculator {

	public Object calculate() throws Exception {
		Warehouse key = new Warehouse(); 
		key.setNumber(4);
		key.setZoneNumber(4);
		return key;
	}

}

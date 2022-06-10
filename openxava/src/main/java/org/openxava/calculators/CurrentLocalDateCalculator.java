package org.openxava.calculators;

import java.time.*;

/**
 * 
 * @since 6.1
 * @author Javier Paniza
 */
public class CurrentLocalDateCalculator implements ICalculator {

	public Object calculate() throws Exception {
		return LocalDate.now();
	}

}

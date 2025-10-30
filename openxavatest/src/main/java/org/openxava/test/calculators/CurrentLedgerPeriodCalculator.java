package org.openxava.test.calculators;

import org.openxava.calculators.*;
import org.openxava.jpa.*;

/**
 *  
 * @author Javier Paniza
 */

public class CurrentLedgerPeriodCalculator implements ICalculator{

	public Object calculate() throws Exception { // It does the calculation
		System.out.println("[CurrentYearFdCalculator.calculate] "); // tmp
	    return XPersistence.getManager()
	    	.createQuery("from LedgerPeriod p where sysdate() between p.startDate and p.endDate")
	        .getResultList().get(0);
	
	}
	
}
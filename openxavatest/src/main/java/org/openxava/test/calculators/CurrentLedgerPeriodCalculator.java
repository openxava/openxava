package org.openxava.test.calculators;

import org.openxava.calculators.*;
import org.openxava.jpa.*;
import org.openxava.test.model.*;

/**
 *  
 * @author Javier Paniza
 */

public class CurrentLedgerPeriodCalculator implements ICalculator{

	public Object calculate() throws Exception { // It does the calculation
		LedgerPeriod p =  (LedgerPeriod) XPersistence.getManager()
	    	.createQuery("from LedgerPeriod p where sysdate() between p.startDate and p.endDate")
	        .getResultList().get(0);
		return p; // Returning entity to test a case
	}
	
}
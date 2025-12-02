package org.openxava.test.calculators;

import javax.persistence.*;

import org.openxava.calculators.*;
import org.openxava.jpa.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 */
	
@Getter @Setter 	
public class NextNumberForLedgerPeriodCalculator implements ICalculator{
	
	String periodId; 
	
	public Object calculate() throws Exception { // It does the calculation
		Query query = XPersistence.getManager() // A JPA query
			.createQuery("select max(l.number) from Ledger l where l.period.id = :periodId"); // The query returns
		// the max invoice number of the indicated year
		query.setParameter("periodId", periodId); // We use the injected year as a parameter for the query
		Integer lastNumber = (Integer) query.getSingleResult();
		return lastNumber == null ? 1 : lastNumber + 1; // Returns the last invoice number
	}

}
package org.openxava.test.calculators;

import java.util.*;

import javax.persistence.*;

import org.openxava.calculators.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 */
public class NextTravelExpensesNumberCalculator implements ICalculator { 
	
    @Getter @Setter 
    Date date; 
 
    public Object calculate() throws Exception {
    	int year = Dates.getYear(date); 
        Query query = XPersistence.getManager() 
            .createQuery("select max(t.number) from TravelExpenses t where t.year = :year"); 
        query.setParameter("year", year); 
        Integer lastNumber = (Integer) query.getSingleResult();
        return lastNumber == null ? 1 : lastNumber + 1;                                                         
    }
 
}
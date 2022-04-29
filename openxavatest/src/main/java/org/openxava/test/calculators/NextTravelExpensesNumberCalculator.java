package org.openxava.test.calculators;

import javax.persistence.*;

import org.openxava.calculators.*;
import org.openxava.jpa.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 */
public class NextTravelExpensesNumberCalculator implements ICalculator { 

    @Getter @Setter 
    int year; 
 
    public Object calculate() throws Exception { 
        Query query = XPersistence.getManager() 
            .createQuery("select max(t.number) from TravelExpenses t where t.year = :year"); 
        query.setParameter("year", year); 
        Integer lastNumber = (Integer) query.getSingleResult();
        return lastNumber == null ? 1 : lastNumber + 1;                                                         
    }
 
}
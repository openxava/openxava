package org.openxava.test.calculators;

import javax.persistence.*;

import org.openxava.calculators.*;
import org.openxava.jpa.*;

import lombok.*;

/**
 * tmr Quitar si al final no la uso
 * 
 * @author Javier Paniza
 */
public class NextQuoteNumberCalculator implements ICalculator { 

    @Getter @Setter 
    int year; 
 
    public Object calculate() throws Exception { 
        Query query = XPersistence.getManager() 
            .createQuery("select max(q.number) from Quote q where q.year = :year"); 
        query.setParameter("year", year); 
        Integer lastNumber = (Integer) query.getSingleResult();
        return lastNumber == null ? 1 : lastNumber + 1;                                                         
    }
 
}
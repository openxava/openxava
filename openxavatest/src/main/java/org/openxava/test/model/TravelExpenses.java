package org.openxava.test.model;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.test.calculators.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity 
@Getter @Setter
@IdClass(TravelExpensesKey.class)
public class TravelExpenses {
	
	@Id
	@DefaultValueCalculator(CurrentYearCalculator.class) 
	@Column(length=4) @Required
	int year;
	
	@Column(length=6) 
	@DefaultValueCalculator(value=NextTravelExpensesNumberCalculator.class,
		properties=@PropertyValue(name="date") 
	)
	@Id
	int number;
	
	@Required @DefaultValueCalculator(CurrentDateCalculator.class)
	Date date;
	
	@ElementCollection
	@ListProperties("description, amount[travelExpenses.total], file")  
	Collection<TravelExpense> expenses;
	
	public BigDecimal getTotal() {
		if (expenses == null) return BigDecimal.ZERO;
		return expenses.stream()
			.map(TravelExpense::getAmount)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
}

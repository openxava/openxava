package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.test.calculators.*;

import lombok.*;

/**
 * tmr
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
	    properties=@PropertyValue(name="year") 
	)
	@Id
	int number;
	
	@Required @DefaultValueCalculator(CurrentDateCalculator.class)
	Date date;
	
	@ElementCollection
	Collection<TravelExpense> expenses;

}

package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.test.calculators.*;

import lombok.*;

/**
 * 
 * @author javi
 */
@Getter @Setter @EqualsAndHashCode
public class TravelExpensesKey implements java.io.Serializable {
	
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

}

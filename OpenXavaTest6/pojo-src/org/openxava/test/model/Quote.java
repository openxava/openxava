package org.openxava.test.model;

import java.math.*;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.model.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
@Getter @Setter  
@Views({
	@View(members="year, number, date; customer; details; estimatedProfit"), 
	@View(name="QuoteWithRemoveElementCollection", members="year, number, date; data { customer; details }") 
})
@Tab(defaultOrder="${year} desc") 
public class Quote extends Identifiable {
		
	@Column(length=4) @Required
	int year;
	
	@Column(length=6) 
	int number;
	
	@Required @DefaultValueCalculator(CurrentDateCalculator.class)
	Date date;
	
	@Digits(integer = 2, fraction = 2)
	@DefaultValueCalculator(value=BigDecimalCalculator.class,
		properties = @PropertyValue(name="value", value="21")	
	)
	BigDecimal taxesRate; 
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@ReferenceView("Simplest")
	Customer customer;
	
	@RemoveSelectedAction(forViews="QuoteWithRemoveElementCollection", value="Quote.removeDetail") 
	@javax.validation.constraints.Size(min=1, max=3)  
	@ElementCollection
	@ListProperties("product.number, product.description, unitPrice, quantity, amount[quote.amountsSum, quote.taxesRate, quote.taxes, quote.total]") 
	Collection<QuoteDetail> details;

	@PrePersist
	private void generateDefaultNumberValue() { 
		if (number == 0) number = 77;
	}
	
	public BigDecimal getAmountsSum() {
		BigDecimal sum = new BigDecimal(0);
		for (QuoteDetail detail: getDetails()) {
			sum = sum.add(detail.getAmount());
		}
		return sum;
	}
	
	@Depends("amountsSum, taxesRate") 
	public BigDecimal getTaxes() {
		return getAmountsSum().multiply(getTaxesRate()).divide(new BigDecimal("100")); 
	}
	
	@Depends("amountsSum, taxes") 
	public BigDecimal getTotal() {
		return getAmountsSum().add(getTaxes());
	}	
	
	@Depends("total") 
	public BigDecimal getEstimatedProfit() { 
		return getTotal().multiply(new BigDecimal("0.1"));
	}
	
}

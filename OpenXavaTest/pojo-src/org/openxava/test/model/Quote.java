package org.openxava.test.model;

import java.math.BigDecimal;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.CurrentDateCalculator;
import org.openxava.model.Identifiable;

import lombok.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
@Getter @Setter // tmp 
@Views({
	@View(members="year, number, date; customer; details; estimatedProfit"), 
	@View(name="QuoteWithRemoveElementCollection", members="year, number, date; data { customer; details }") 
})
@Tab(defaultOrder="${year} desc") 
public class Quote extends Identifiable {
		
	@Column(length=4) @Required
	/* tmp private */ int year;
	
	@Column(length=6) 
	/* tmp private */ int number;
	
	@Required @DefaultValueCalculator(CurrentDateCalculator.class)
	/* tmp private */ Date date;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@ReferenceView("Simplest")
	/* tmp private */ Customer customer;
	
	@RemoveSelectedAction(forViews="QuoteWithRemoveElementCollection", value="Quote.removeDetail") 
	@javax.validation.constraints.Size(min=1, max=3)  
	@ElementCollection
	@ListProperties("product.number, product.description, unitPrice, quantity, amount[quote.amountsSum, quote.taxes, quote.total]")
	/* tmp private */ Collection<QuoteDetail> details;

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
	
	public BigDecimal getTaxes() {
		return getAmountsSum().multiply(new BigDecimal("0.21"));
	}
	
	public BigDecimal getTotal() {
		return getAmountsSum().add(getTaxes());
	}	
	
	@Depends("total") 
	public BigDecimal getEstimatedProfit() { 
		return getTotal().multiply(new BigDecimal("0.1"));
	}
	
	/* tmp
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Collection<QuoteDetail> getDetails() {
		return details;
	}

	public void setDetails(Collection<QuoteDetail> details) {
		this.details = details;
	}
	*/

}

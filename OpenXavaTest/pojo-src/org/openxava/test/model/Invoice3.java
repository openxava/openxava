package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.hibernate.annotations.Type;
import org.openxava.annotations.*;
import org.openxava.calculators.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@IdClass(InvoiceKey.class)  // We reuse the key class for Invoice
@Table(name="INVOICE")
@Tabs({
	@Tab(name="Ascending", defaultOrder="${number} asc") 
})
public class Invoice3 {
	
	@Id @Column(length=4) @Max(9999) @Required
	@DefaultValueCalculator(CurrentYearCalculator.class)
	private int year;
	
	@Id @Column(length=6) @Required
	private int number;
		
	@Required
	@DefaultValueCalculator(CurrentDateCalculator.class)
	private java.util.Date date;
	
	@Digits(integer=2, fraction=1)
	@Required
	private BigDecimal vatPercentage;
	
	@Column(length=50)
	private String comment;
	
	@Type(type="org.openxava.types.SiNoType")
	private boolean paid;
		
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@ReferenceView("Simple")
	@ReferenceViews(
		@ReferenceView(forViews="CustomerAsAggregateWithDeliveryPlaces", value="SimpleWithDeliveryPlaces")	
	)
	@AsEmbedded(forViews="CustomerAsAggregateWithDeliveryPlaces")
	private Customer customer;
	
	
	public java.util.Date getDate() {
		return date;
	}

	public void setDate(java.util.Date date) {
		this.date = date;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public BigDecimal getVatPercentage() {
		return vatPercentage==null?BigDecimal.ZERO:vatPercentage;
	}

	public void setVatPercentage(BigDecimal vatPercentage) {
		this.vatPercentage = vatPercentage;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}

package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.openxava.annotations.*;
import org.openxava.calculators.*;

/**
 * 
 * @author Javier Paniza
 */

@TypeDef( 
	name="si_no",
	typeClass=org.openxava.types.SiNoType.class
)

@Entity
@IdClass(InvoiceKey.class)  // We reuse the key class for Invoice
@Table(name="INVOICE")
public class Invoice4 {
	
	@Id @Column(length=4) @Max(9999l) @Required
	@DefaultValueCalculator(CurrentYearCalculator.class)
	private int year;
	
	@Id @Column(length=6) @Required
	private int number;
		
	@Required
	@DefaultValueCalculator(CurrentDateCalculator.class)
	private java.util.Date date;
		
	@Type(type="si_no")	
	private boolean paid;
	
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

	public java.util.Date getDate() {
		return date;
	}

	public void setDate(java.util.Date date) {
		this.date = date;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

}

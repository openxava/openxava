package org.openxava.invoicedemo.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

@Entity @Getter @Setter
public class Product {
	
	@Id @Column(length=9)
	int number;
	
	@Column(length=40) @Required
	String description;

	@Required
	BigDecimal unitPrice;
	
	@Files
	@Column(length=32)
	String photos;
	
	/*
	Integer availableUnits; // tmr
	
	Integer disposableUnits; // tmr
	
	Integer units; // tmr
	
	Integer futureUnits; // tmr
	
	Integer oldUnits; // tmr
	*/

	/*
	public BigDecimal getVat() { // tmr
		return new BigDecimal(21);
	}
	*/

}

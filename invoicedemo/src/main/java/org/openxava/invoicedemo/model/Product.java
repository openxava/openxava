package org.openxava.invoicedemo.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

@Entity @Getter @Setter
@Tab(editableProperties = "description, unitPrice")
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

}

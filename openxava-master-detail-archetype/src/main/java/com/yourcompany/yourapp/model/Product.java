package com.yourcompany.yourapp.model;

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

}

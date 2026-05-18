package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;
import javax.validation.constraints.Digits;

import org.openxava.annotations.*;

import lombok.*;

@Embeddable @Getter @Setter
public class ContributionDetail {
	
	@Column(length = 40) @Required
	String description;
	
	double amount; // double for test 
	
	int pieces;
	
	float tax; // float for test
	
	@Money @ReadOnly
	@Digits(integer=10, fraction=0)
	@Calculation("amount * pieces * 1.003 - tax") // It can generate decimals
	BigDecimal total; 

}

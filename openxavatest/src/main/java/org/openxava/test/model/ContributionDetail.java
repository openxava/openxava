package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

@Embeddable @Getter @Setter
public class ContributionDetail {
	
	@Column(length = 40) @Required
	String description;
	
	double amount; // double for test 
	
	int pieces;
	
	float tax;
	
	@Money @ReadOnly
	@Calculation("(amount * pieces) - tax")
	BigDecimal total; // float for test

}

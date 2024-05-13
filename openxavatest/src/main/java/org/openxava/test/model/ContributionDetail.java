package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

@Embeddable @Getter @Setter
public class ContributionDetail {
	
	@Column(length = 40) @Required
	String description;
	
	//@Money
	double amount; // double for test 
	
	int pieces;
	
	@Money @ReadOnly
	@Calculation("amount * pieces")
	BigDecimal total;

}

package org.openxava.chattest.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import org.openxava.chattest.calculators.*;

import lombok.*;

@Embeddable @Getter @Setter
public class Detail {
		
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	Product product;

	@Required 
	@DefaultValueCalculator(  
		value=UnitPriceCalculator.class,
		properties=@PropertyValue(
			name="number",
			from="product.number")
	)
	BigDecimal unitPrice;
		
	@Required
	int quantity;	

	@ReadOnly @Money
	@Calculation("unitPrice * quantity") 
	BigDecimal amount;
	
}

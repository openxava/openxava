package org.openxava.test.model;

import java.math.*;

import jakarta.persistence.*;

import org.openxava.annotations.*;
import org.openxava.test.calculators.*;

import lombok.*;

@Embeddable @Getter @Setter
public class ReceiptDetail {
		
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	Product product;

	@Required 
	@DefaultValueCalculator(  
		value=UnitPriceCalculator.class,
		properties=@PropertyValue(
			name="productNumber",
			from="product.number")
	)
	BigDecimal unitPrice;
		
	@Required
	int quantity;	

	@ReadOnly @Money
	@Calculation("unitPrice * quantity") 
	BigDecimal amount;
	
}

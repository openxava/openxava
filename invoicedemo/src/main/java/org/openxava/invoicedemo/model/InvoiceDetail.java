package org.openxava.invoicedemo.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.invoicedemo.calculators.*;

import lombok.*;

@Embeddable @Getter @Setter
public class InvoiceDetail {
		
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

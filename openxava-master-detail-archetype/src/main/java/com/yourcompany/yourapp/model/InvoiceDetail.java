package com.yourcompany.yourapp.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.yourcompany.yourapp.calculators.*;

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

	@Depends("unitPrice, quantity") 
	public BigDecimal getAmount() {
		return new BigDecimal(getQuantity()).multiply(getUnitPrice()); 
	}


	public BigDecimal getUnitPrice() {
		return unitPrice == null?new BigDecimal("0.00"):unitPrice;
	}
	
}

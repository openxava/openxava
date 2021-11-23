package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.test.calculators.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza 
 */

@Embeddable
@Getter @Setter 
public class QuoteDetail {
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	Product product;
	
	@DefaultValueCalculator( 
		value=UnitPriceCalculator.class,
		properties=@PropertyValue(
			name="productNumber",
			from="product.number")
	)
	@Stereotype("MONEY") 
	BigDecimal unitPrice;
	
	int quantity;
	 		
	@Column(precision=10, scale=2)
	@Depends("unitPrice, quantity")
	public BigDecimal getAmount() {
		return getUnitPrice().multiply(new BigDecimal(getQuantity()));
	}

}

package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;

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
	
	/* tmr 
	@DefaultValueCalculator( 
		value=UnitPriceCalculator.class,
		properties=@PropertyValue(
			name="productNumber",
			from="product.number")
	)
	*/
	// tmr ini
	// tmr Esto reproduce el problema informado exactamente
	/* tmr */
	@DefaultValueCalculator( 
		value=BigDecimalCalculator.class,
		properties=@PropertyValue(
			name="value",
			value="666")
	)	
	
	// tmr fin
	@Stereotype("MONEY") 
	BigDecimal unitPrice;
	
	int quantity;
	 		
	@Column(precision=10, scale=2)
	@Depends("unitPrice, quantity")
	public BigDecimal getAmount() {
		return getUnitPrice().multiply(new BigDecimal(getQuantity()));
	}

}

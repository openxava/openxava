package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza 
 */

@Embeddable
@Getter @Setter 
public class QuoteDetail {
	
	/* tmr 
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
	*/
	
	// tmr ini
	// TMR Cuando esté arreglado en Order comprobar que esto de abajo funciona, si funciona dejar todo como está
	@DescriptionsList
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	Product product;
	
	@Stereotype("MONEY") 
	public BigDecimal getUnitPrice() {
		if (product == null) return BigDecimal.ZERO;
		return product.getUnitPrice();
	}
	
	public int getQuantity() {
		return 1;
	}

	 		
	@Column(precision=10, scale=2)
	@Depends("product.number")
	public BigDecimal getAmount() {
		return getUnitPrice().multiply(new BigDecimal(getQuantity()));
	}
	// tmr fin

}

package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.test.calculators.*;

/**
 * 
 * @author Javier Paniza 
 */

@Embeddable
public class QuoteDetail {
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	private Product product;
	
	@DefaultValueCalculator( 
		value=UnitPriceCalculator.class,
		properties=@PropertyValue(
			name="productNumber",
			from="product.number")
	)
	@Stereotype("MONEY") 
	private BigDecimal unitPrice;
	
	private int quantity;
	 		
	@Column(precision=10, scale=2)
	@Depends("unitPrice, quantity")
	public BigDecimal getAmount() {
		return getUnitPrice().multiply(new BigDecimal(getQuantity()));
	}

	public BigDecimal getUnitPrice() {
		return unitPrice; 
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}

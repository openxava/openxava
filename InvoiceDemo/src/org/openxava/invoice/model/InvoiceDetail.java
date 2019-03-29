package org.openxava.invoice.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.invoice.calculators.*;

@Embeddable
public class InvoiceDetail {
		
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	private Product product;

	@Required 
	@DefaultValueCalculator(  
		value=UnitPriceCalculator.class,
		properties=@PropertyValue(
			name="productNumber",
			from="product.number")
	)
	private BigDecimal unitPrice;
		
	@Required
	private int quantity;
	

	@Depends("unitPrice, quantity") 
	public BigDecimal getAmount() {
		return new BigDecimal(getQuantity()).multiply(getUnitPrice()); 
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

	public BigDecimal getUnitPrice() {
		return unitPrice == null?new BigDecimal("0.00"):unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	
}

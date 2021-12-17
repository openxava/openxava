package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

@Entity
@View(members="product; quantity, amount")  
@View(name="ProductAsDescriptionsList", members="product; quantity, amount") // tmr
public class OrderDetail extends Identifiable {
	
	@ManyToOne // Lazy fetching fails on removing a detail from parent
	private Order parent;
		
	private int quantity;
	
	// @DescriptionsList(forViews="ProductAsDescriptionsList") // tmr
	@DescriptionsList // tmr
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@ReferenceView("SimpleWithFamily") 
	private Product2 product;
	
	@Stereotype("MONEY")  
	@Depends("product.number, quantity") 
	public BigDecimal getAmount() {
		return new BigDecimal(getQuantity()).multiply(getProduct().getUnitPrice());
	}

	public Order getParent() {
		return parent;
	}

	public void setParent(Order parent) {
		this.parent = parent;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Product2 getProduct() {
		return product;
	}

	public void setProduct(Product2 product) {
		this.product = product;
	}
	
}

package org.openxava.test.model;

import org.openxava.model.*;
import java.math.*;
import javax.persistence.*;
import org.openxava.annotations.*;

@Entity
@View(members="product; quantity, amount")  
public class OrderDetail extends Identifiable {
	
	@ManyToOne // Lazy fetching fails on removing a detail from parent
	private Order parent;
		
	private int quantity;
	
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

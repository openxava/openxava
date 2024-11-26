package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Chungyen Tsai
 */

@Embeddable
@Table(name="REALLOCATION_DETAILS")
public class ReallocationDetail2 {
	
	@Column(length=20)
	@Editor("PlaceName")
	private String place; 
	
	@DefaultValueCalculator( 
		value=org.openxava.calculators.IntegerCalculator.class,
		properties={ @PropertyValue(name="value", value="1") }		
	)
	@ManyToOne(fetch=FetchType.LAZY, optional=true)
	private Product7 product;
	
	private boolean done; 

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public Product7 getProduct() {
		return product;
	}

	public void setProduct(Product7 product) {
		this.product = product;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

}

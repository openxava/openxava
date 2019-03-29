package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.test.calculators.*;

/**
 * 
 * @author Javier Paniza
 */

@Embeddable
public class ProductExpense2 { 
	
	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList	
	private Carrier carrier;


	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties="year, number")
	@DefaultValueCalculator(DefaultInvoiceCalculator.class) 		
	private Invoice invoice;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	@DefaultValueCalculator( 
		value=org.openxava.calculators.IntegerCalculator.class,
		properties={ @PropertyValue(name="value", value="2") }		
	)	
	private Product product;	


	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Carrier getCarrier() {
		return carrier;
	}

	public void setCarrier(Carrier carrier) {
		this.carrier = carrier;
	}
	
	
	
}

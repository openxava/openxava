package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Embeddable
public class ProductExpense {

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties="year, number")
	private Invoice invoice;
		
	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	private Product product;	

	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList	
	@DefaultValueCalculator( 
		value=org.openxava.calculators.IntegerCalculator.class,
		properties={ @PropertyValue(name="value", value="3") }		
	)	
	private Carrier carrier;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	private Family2 family;	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(
		depends="family",
		condition="${family.number} = ?"
	)
	private Subfamily2 subfamily;


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
	
	public Family2 getFamily() {
		return family;
	}

	public void setFamily(Family2 family) {
		this.family = family;
	}

	public Subfamily2 getSubfamily() {
		return subfamily;
	}

	public void setSubfamily(Subfamily2 subfamily) {
		this.subfamily = subfamily;
	}
		
}

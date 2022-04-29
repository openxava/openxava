package org.openxava.test.model;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.test.actions.*;

/**
 * 
 * @author Javier Paniza
 */
@Embeddable
public class Visit {
	
	@Column(length=40) @Required
	private String description;
	
	@Max(99) @OnChange(OnChangeVisitKmAction.class)
	private int km;

	@OnChange(OnChangeVisitCustomerAction.class)
	@ManyToOne(fetch=FetchType.LAZY)	
	private Customer customer;	
	
	@OnChange(OnChangeVisitProductAction.class)
	@DescriptionsList
	@ManyToOne(fetch=FetchType.LAZY)	
	private Product product;	

	@OnChangeSearch(OnChangeSearchVisitCarrierAction.class)
	@ManyToOne(fetch=FetchType.LAZY)
	@NoSearch 
	private Carrier carrier;
	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getKm() {
		return km;
	}

	public void setKm(int km) {
		this.km = km;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
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

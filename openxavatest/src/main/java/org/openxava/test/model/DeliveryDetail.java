package org.openxava.test.model;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class DeliveryDetail {
		
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="DELIVERY_INVOICE_YEAR", referencedColumnName="INVOICE_YEAR"),
		@JoinColumn(name="DELIVERY_INVOICE_NUMBER", referencedColumnName="INVOICE_NUMBER"),
		@JoinColumn(name="DELIVERY_TYPE_NUMBER", referencedColumnName="TYPE"),
		@JoinColumn(name="DELIVERY_NUMBER", referencedColumnName="NUMBER")
	})
	private Delivery delivery;		
	
	@Id
	private int number;
	
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Delivery getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

}

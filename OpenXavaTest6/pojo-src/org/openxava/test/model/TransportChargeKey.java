package org.openxava.test.model;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */
public class TransportChargeKey implements java.io.Serializable {

	@Id @ManyToOne(fetch=FetchType.LAZY)
	/* This the default mapping (at least with Hibernate)
	@JoinColumns({ 
		@JoinColumn(name="DELIVERY_INVOICE_YEAR", referencedColumnName="INVOICE_YEAR"),
		@JoinColumn(name="DELIVERY_INVOICE_NUMBER", referencedColumnName="INVOICE_NUMBER"),
		@JoinColumn(name="DELIVERY_TYPE", referencedColumnName="TYPE"),
		@JoinColumn(name="DELIVERY_NUMBER", referencedColumnName="NUMBER")
	})
	*/
	private Delivery delivery;

	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		return obj.toString().equals(this.toString());
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public String toString() {
		return "TransportChargeKey::" + delivery.getInvoice().getYear() + ":" + delivery.getInvoice().getNumber() + ":" + delivery.getType().getNumber()  + ":" + delivery.getNumber();
	}

	public Delivery getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}


}

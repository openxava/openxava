package org.openxava.test.model;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */
public class TransportChargeKey implements java.io.Serializable {

	/* tmr 
	@Id @ManyToOne(fetch=FetchType.LAZY)
	// This the default mapping (at least with Hibernate) // tmr comentar el mapeo  
	@JoinColumns({ 
		@JoinColumn(name="DELIVERY_INVOICE_YEAR", referencedColumnName="INVOICE_YEAR"),
		@JoinColumn(name="DELIVERY_INVOICE_NUMBER", referencedColumnName="INVOICE_NUMBER"),
		@JoinColumn(name="DELIVERY_TYPE", referencedColumnName="TYPE"),
		@JoinColumn(name="DELIVERY_NUMBER", referencedColumnName="NUMBER")
	})
	private Delivery delivery;
	*/
	
	// tmr ini	
	@Id @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({ 
		@JoinColumn(name="DELIVERY_INVOICE_YEAR", referencedColumnName="YEAR"),
		@JoinColumn(name="DELIVERY_INVOICE_NUMBER", referencedColumnName="NUMBER"),		
	})	
	private Invoice delivery_invoice;	

	@Id @ManyToOne(fetch=FetchType.LAZY)  
	@JoinColumn(name="DELIVERY_TYPE") 
	private DeliveryType delivery_type; 

	@Id @Column(name="DELIVERY_NUMBER")
	private int delivery_number;	
	
	public Invoice getDelivery_invoice() {
		return delivery_invoice;
	}

	public void setDelivery_invoice(Invoice delivery_invoice) {
		this.delivery_invoice = delivery_invoice;
	}

	public DeliveryType getDelivery_type() {
		return delivery_type;
	}

	public void setDelivery_type(DeliveryType delivery_type) {
		this.delivery_type = delivery_type;
	}

	public int getDelivery_number() {
		return delivery_number;
	}

	public void setDelivery_number(int delivery_number) {
		this.delivery_number = delivery_number;
	}
	// tmr fin

	
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
	public String toString() { // tmr
		return "TransportChargeKey [delivery_invoice=" + delivery_invoice + ", delivery_type=" + delivery_type
				+ ", delivery_number=" + delivery_number + "]";
	}
	
	/* tmr
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
	*/

}

package org.openxava.test.model;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */
public class DeliveryKey implements java.io.Serializable {

	@Id @ManyToOne(fetch=FetchType.LAZY)
	private Invoice invoice; 
	@Id @ManyToOne(fetch=FetchType.LAZY)  
	@JoinColumn(name="TYPE") 
	private DeliveryType type; 

	private int number;
	
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
		return "DeliveryKey::" + invoice.getYear() + ":" + invoice.getNumber() + ":" + type.getNumber()  + ":" + number;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public DeliveryType getType() {
		return type;
	}

	public void setType(DeliveryType type) {
		this.type = type;
	}

}

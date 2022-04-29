package org.openxava.test.model;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */
public class TransportCharge2Key implements java.io.Serializable {
	
	@Id  
	private int year;
	
	@Id
	private Integer delivery_invoice_number;
	@Id
	private Integer delivery_type;
	@Id
	private Integer delivery_number;

	
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
		return "TransportCharge2Key::" + getYear() + ":" + delivery_invoice_number + ":" + delivery_type  + ":" + delivery_number;
	}
	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Integer getDelivery_invoice_number() { 
		return delivery_invoice_number;
	}

	public void setDelivery_invoice_number(Integer delivery_invoice_number) { 
		this.delivery_invoice_number = delivery_invoice_number;
	}

	public Integer getDelivery_number() { 
		return delivery_number;
	}

	public void setDelivery_number(Integer delivery_number) { 
		this.delivery_number = delivery_number;
	}

	public Integer getDelivery_type() { 
		return delivery_type;
	}

	public void setDelivery_type(Integer delivery_type) { 
		this.delivery_type = delivery_type;
	}

}

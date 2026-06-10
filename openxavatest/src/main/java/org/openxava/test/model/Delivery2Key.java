package org.openxava.test.model;

import jakarta.persistence.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 */
@Getter @Setter
public class Delivery2Key implements java.io.Serializable {

	@Id @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="invoice_year", referencedColumnName="year"),
		@JoinColumn(name="invoice_number", referencedColumnName="number")
	})
	Invoice delinv;

	@Id @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="TYPE")
	DeliveryType type;

	int number;

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
		return "Delivery2Key::" + delinv.getYear() + ":" + delinv.getNumber() + ":" + type.getNumber() + ":" + number;
	}

}

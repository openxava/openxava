package org.openxava.test.model;

import javax.persistence.*;

/**
 * tmp
 * @author Javier Paniza
 */

@MappedSuperclass
public abstract class TaxpayerBase extends Nameable {

	@Column(length=80)
	private String address;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
}
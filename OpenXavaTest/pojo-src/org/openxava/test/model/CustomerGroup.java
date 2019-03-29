package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class CustomerGroup extends Nameable {
	
	@ListProperties("number, name, address.state.name") 
	@OneToMany(mappedBy="group", cascade=CascadeType.REMOVE) // CascadeType.REMOVE to test a case 
	private Collection<Customer> customers;

	public Collection<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(Collection<Customer> customers) {
		this.customers = customers;
	}

}

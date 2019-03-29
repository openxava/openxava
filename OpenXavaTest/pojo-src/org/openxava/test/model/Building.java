package org.openxava.test.model;

import org.openxava.test.actions.*;
import org.openxava.annotations.*;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */
@Entity
@Views({
	@View(members="building [name, function; address]"), // All data in a group for a test	
	@View(name="Simple", members="name")
})
@Tab( properties= "name, address.street, address.zipCode, address.city" )
public class Building extends Nameable {
	
	@ManyToOne
	private Company company;
	
	@Column(length=40)
	@OnChange(OnChangeVoidAction.class) 
	private String function; 
	
	@AttributeOverride(name="street",
		column=@Column(name="BSTREET")) 
	@AttributeOverride(name="zipCode",
		column=@Column(name="BZIPCODE"))
	private Address address;

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
}

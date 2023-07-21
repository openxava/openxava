package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.test.actions.*;

/**
 * 
 * @author Javier Paniza 
 */
@Entity
@Views({
	@View(members="building [name, function; address; mailingAddress]"), // All data in a group for a test 
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
	@AttributeOverride(name="zip", // Needed to test a case, where attribute is private
		column=@Column(name="BZIPCODE")) // with no corresponding getter and setter 
	private Address address;
	
	@AttributeOverride(name="street",
		column=@Column(name="MAILING_STREET"))
	@AttributeOverride(name="zip",
		column=@Column(name="MAILING_ZIPCODE"))	
	@AttributeOverride(name="city",
		column=@Column(name="MAILING_CITY"))
	@AssociationOverride(name = "state",
		joinColumns = @JoinColumn(name = "MAILING_STATE"))
	private Address mailingAddress;

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

	public Address getMailingAddress() {
		return mailingAddress;
	}

	public void setMailingAddress(Address mailingAddress) {
		this.mailingAddress = mailingAddress;
	}
	
}

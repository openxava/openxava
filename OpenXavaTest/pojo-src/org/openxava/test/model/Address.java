package org.openxava.test.model;

import javax.persistence.*;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Embeddable
@Views ({
	@View( members =
		"viewProperty;" +
		"street, zipCode, Address.addFullAddress();" +		
		"city [" +
		"	city, state;" +
		"];" + // Don't remove the ; to test a case
		"asString"
	),
	@View( name="StateAsForm" ),
	@View( name="Demo", members = 
		"street, zipCode;" +		
		"city, state;"
	)
})
public class Address implements IWithCity {

	@Transient
	private Customer customer;
	
	@Transient @Action("Address.fillViewProperty")
	private String viewProperty;	
	
	// By default label format is normal, therefore in this case @LabelFormat is not needed
	@Required @Column(length=30) @LabelFormat(LabelFormatType.NORMAL)
	@LabelFormats({ 
		@LabelFormat(forViews="Demo", value=LabelFormatType.SMALL)
	})
	@Action(value="Customer.prefixStreet", alwaysEnabled=true)
	private String street;
	
	@Required @Column(length=5) 
	@LabelFormat(LabelFormatType.SMALL)	
	private int zipCode;
	
	
	@Required @Column(length=20)
	@LabelFormat(LabelFormatType.NO_LABEL)
	@LabelFormat(forViews="Demo", value=LabelFormatType.SMALL)
	private String city;
	
	// ManyToOne inside an Embeddable is not supported by JPA 1.0 (see at 9.1.34),
	// but Hibernate implementation supports it.
	@DescriptionsList(notForViews="StateAsForm") @LabelFormat(LabelFormatType.SMALL)
	@ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="STATE")	
	private State state;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public int getZipCode() {
		return zipCode;
	}

	public void setZipCode(int zipCode) {
		this.zipCode = zipCode;
	}
	
	@Depends("customer.number") 
	public String getAsString() {
		return getStreet() + getZipCode() + getCity() + getStateNameAsString() + getCustomerNumberAsString(); 
	}
	
	private String getStateNameAsString() { 
		return getState() == null?"":getState().getName();
	}
	
	private String getCustomerNumberAsString() { 
		return getCustomer() == null?"":Integer.toString(getCustomer().getNumber());
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getViewProperty() {
		return viewProperty;
	}

	public void setViewProperty(String viewProperty) {
		this.viewProperty = viewProperty;
	}

	Customer getCustomer() {
		return customer;
	}

	void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
}

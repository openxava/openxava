package org.openxava.invoice.model;

import javax.persistence.*;

import org.openxava.annotations.*;

@Entity
@View(members="customer [ number; name; photo; address; city; country ], location")
@View(name="Simple", members="number, name") 
@Tab(properties="number, name") 
public class Customer {
	
	@Id
	private int number;
	
	@Column(length=40) 
	private String name;
	
	@Stereotype("PHOTO")
	@Basic(fetch=FetchType.LAZY)
	private byte [] photo;
	
	@Column(length=40) 
	private String address; 	

	@Column(length=40)
	private String city; 
	
	@Column(length=40)
	private String country; 
	
	@Stereotype("COORDINATES") 
	@Column(length=50)
	private String location; 

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte [] getPhoto() {
		return photo;
	}

	public void setPhoto(byte [] photo) {
		this.photo = photo;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
}

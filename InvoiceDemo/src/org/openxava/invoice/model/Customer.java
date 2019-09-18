package org.openxava.invoice.model;

import javax.persistence.*;

import org.openxava.annotations.*;

@Entity
@View(name="Simple", members="number, name") 
public class Customer {
	
	@Id
	private int number;
	
	@Column(length=40) 
	private String name;
	
	@Stereotype("PHOTO")
	private byte [] photo; 

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
	
}

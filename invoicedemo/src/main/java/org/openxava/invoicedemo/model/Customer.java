package org.openxava.invoicedemo.model;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

@Entity @Getter @Setter
@View(members="customer [ number; name; photo; address; city; country ], location")
@View(name="Simple", members="number, name") 
@Tab(properties="number, name, photo") 
public class Customer {
	
	@Id
	int number;
	
	@Column(length=40) 
	String name;
	
	@File
	@Column(length=32)
	String photo;
	
	@Column(length=40) 
	String address; 	

	@Column(length=40)
	String city; 
	
	@Column(length=40)
	String country; 
	
	@Stereotype("COORDINATES") 
	@Column(length=50)
	String location;
	
}

package org.openxava.chatvoice.model;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

@Entity @Getter @Setter
@View(members="data [ number; name; photo; address; city ], location") // With no country to test a case
@View(name="Simple", members="number, name") 
@Tab(properties="number, name, photo, address, city, country")
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

	@ReadOnly // To test a case
	@Column(length=40)
	String city; 
	
	@Column(length=40)
	String country; 
	
	@Coordinates 
	@Column(length=50)
	String location; 
	
}

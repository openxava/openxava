package com.yourcompany.yourapp.model;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

// RENAME THIS CLASS AS Customer, Client, Worker, Owner, Seller, etc.

// YOU CAN RENAME THE MEMBERS BELOW AT YOUR CONVENIENCE, 
// FOR EXAMPLE country BY state,
// BUT CHANGE ALL REFERENCES IN ALL CODE USING SEARCH AND REPLACE FOR THE PROJECT. 
// DON'T USE REFACTOR > RENAME FOR MEMBERS BECAUSE IT DOESN'T CHANGE THE ANNOTATIONS CONTENT.

@Entity @Getter @Setter
@View(members="data [ number; name; photo; address; city; country ], location")
@View(name="Simple", members="number, name") 
@Tab(properties="number, name, photo") 
public class Person {
	
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
	
	@Coordinates 
	@Column(length=50)
	String location; 
	
}

package org.openxava.chatvoice.model;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

@Entity @Getter @Setter
// tmr @View(members="data [ number; name; photo; address; city; country ], location")
@View(members="data [ number; name; photo; address; city ], location") // Quitamos country
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

	@ReadOnly // tmr
	@Column(length=40)
	String city; 
	
	@Column(length=40)
	String country; 
	
	@Coordinates 
	@Column(length=50)
	String location; 
	
}

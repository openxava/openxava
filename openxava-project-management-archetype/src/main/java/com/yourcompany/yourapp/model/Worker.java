package com.yourcompany.yourapp.model;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.jpa.*;

import lombok.*;

@Entity @Getter @Setter
public class Worker extends Nameable { 
	
	/* It could be in this way with XavaPro
	@DescriptionsList
	@ManyToOne(fetch = FetchType.LAZY)
	User user;  
	*/
	
	@Column(length=30)
	String userName; 
	
	@Column(length=60) @Email 
	String email; 
	
	public static Worker findById(String id) {  
		return XPersistence.getManager().find(Worker.class, id);
	}
	
}

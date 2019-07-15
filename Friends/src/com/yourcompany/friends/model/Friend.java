package com.yourcompany.friends.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

@Entity
public class Friend extends Identifiable {
	
	@Required @Column(length=40)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

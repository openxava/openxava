package org.openxava.test.model;


import org.openxava.model.*;

import javax.persistence.*;

@Entity
public class Country extends Identifiable {

	@Column(length=20)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}

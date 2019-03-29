package org.openxava.test.model;

import javax.persistence.*;

@Entity
public class StreetShop extends Shop {

	private String city;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
}

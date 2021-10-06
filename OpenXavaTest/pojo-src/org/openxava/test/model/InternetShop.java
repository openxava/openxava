package org.openxava.test.model;

import javax.persistence.*;

import org.hibernate.validator.constraints.*;

@Entity
public class InternetShop extends Shop {
	
	@Column(length=60) 
	// tmr @Stereotype("WEBURL")
	@URL // tmr
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}

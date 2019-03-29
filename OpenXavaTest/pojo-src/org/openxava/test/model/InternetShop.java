package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

@Entity
public class InternetShop extends Shop {
	
	@Column(length=60) @Stereotype("WEBURL")
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}

package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Embeddable
public class CarPhoto {

	@Stereotype("PHOTO")
	private byte [] photo;
	
	@Column(length=50)
	private String description; 

	public byte [] getPhoto() {
		return photo;
	}

	public void setPhoto(byte [] photo) {
		this.photo = photo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}

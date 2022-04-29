package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Jeromy Altuna
 */
@Embeddable
public class Attachments {
	
	@Column(length=32) @Stereotype("FILE")
	private String photo;
		
	@Column(length=32) @Stereotype("FILES")
	private String documents;
		
	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getDocuments() {
		return documents;
	}

	public void setDocuments(String documents) {
		this.documents = documents;
	}	
}

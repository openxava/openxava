package org.openxava.test.model;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@Table(name="ArtistLevel")
public class ActingLevel {
	
	
	@Id
	@Column(length = 10, columnDefinition = "char(10)")
	private String id;
	
	@Column(length = 40, columnDefinition = "char(40)")
	private String description;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}

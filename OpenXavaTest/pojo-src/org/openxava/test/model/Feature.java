package org.openxava.test.model;

import javax.persistence.*;

/**
 *
 * @author Javier Paniza
 */
@Embeddable
public class Feature {

	@Column(length=80)
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}

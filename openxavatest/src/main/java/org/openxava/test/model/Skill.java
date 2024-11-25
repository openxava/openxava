package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza
 */
@Entity
@Tab(defaultOrder="${description} asc") 
public class Skill extends Identifiable {

	// tmr @Column(length=60) @Required
	@Column(length=130) @Required // tmr
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
			
}

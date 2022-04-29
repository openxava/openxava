package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
public class InventDetail extends Identifiable {
	
	@ManyToOne
	private Invent invent;
	
	@Column(length=40) @Required
	private String description;

	public Invent getInvent() {
		return invent;
	}

	public void setInvent(Invent invent) {
		this.invent = invent;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}

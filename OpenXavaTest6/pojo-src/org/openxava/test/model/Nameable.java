package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * Base class for entities with a 'name' property. <p> 
 * 
 * @author Javier Paniza
 */
@MappedSuperclass
public class Nameable extends Identifiable {

	@Column(length=50) @Required
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}

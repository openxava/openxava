package org.openxava.test.model;

import javax.persistence.*;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza 
 */
public class Name {
	
	@Column(length=40) @Required
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

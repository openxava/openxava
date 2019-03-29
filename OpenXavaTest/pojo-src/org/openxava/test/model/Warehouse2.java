package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * For testing @EmbeddedId
 * 
 * @author Javier Paniza
 */

@Entity
@Table(name="WAREHOUSE")
public class Warehouse2 {
	
	@EmbeddedId 	
	private Warehouse2Key key;	
		
	@Column(length=40) @Required
	private String name;
	
	public Warehouse2Key getKey() {
		return key;
	}

	public void setKey(Warehouse2Key key) {
		this.key = key;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
		
}

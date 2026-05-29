package org.openxava.model;

import jakarta.persistence.*;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

/**
 * Base class for defining entities with a UUID id. <p>
 * 
 * @author Javier Paniza
 */

@MappedSuperclass
public class Identifiable {
	
	@Id @GeneratedValue @Hidden 
	@UuidGenerator
	@Column(length=32)
	private String id;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

}

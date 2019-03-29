package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Sebastien Diot 
 */

@Entity
public class WallEntry extends AbtractWallEntry<Wall> {
	
	@Required @Column(length=80)
	private String message;

	public void setMessage(String message) {
		this.message = message;
	}

	
	public String getMessage() {
		return message;
	}
	
}

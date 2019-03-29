package org.openxava.test.model;


import javax.persistence.*;
import org.openxava.annotations.*;

/**
 * 
 * @author Sebastien Diot 
 */

@Entity
@View(members="name; entries")
public class Wall extends AbstractWall<WallEntry> {

	@Required @Column(length=40)
	private String name;

	public void setName(String name) {
		this.name = name;
	}
	 
	public String getName() {
		return name;
	}
		
}

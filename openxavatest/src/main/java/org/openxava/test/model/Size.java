package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@Tab(properties="id, name")
public class Size {
	
	@SequenceGenerator(name="SIZE_SEQ", sequenceName="SIZE_ID_SEQ", allocationSize=1 )
	@Hidden @Id @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SIZE_SEQ")
	private int id;
	
	@Column(length=20) @Required 
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

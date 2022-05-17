package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Tab(properties="id, name, fullName")
@Entity @Table(schema="XAVATEST")
public class State {
	
	@Id @Column(length=2) @Required
	private String id;

	@Required @Column(length=20)
	private String name;
	
	public String getFullName() {
		return getId() + " " + getName();
	}
	
	/*
	 * When formula starts with some function it fails in the model that use State like reference when you 
	 * 	put state.fullNameWithFormula at the tab
	 */
	@org.hibernate.annotations.Formula("CONCAT(id,name)")
	private String fullNameWithFormula;
	
	public String getFullNameWithFormula(){
		return fullNameWithFormula;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
		
}

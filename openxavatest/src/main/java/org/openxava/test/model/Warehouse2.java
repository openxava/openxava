package org.openxava.test.model;

import java.util.*;

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
		
	@OneToMany(mappedBy = "warehouse", cascade = CascadeType.REMOVE)
	private Collection<Warehouse2Building> buildings;  
	
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

	public Collection<Warehouse2Building> getBuildings() {
		return buildings;
	}

	public void setBuildings(Collection<Warehouse2Building> buildings) {
		this.buildings = buildings;
	}	
		
}

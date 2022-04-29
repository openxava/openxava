package org.openxava.test.model;

import org.openxava.annotations.*;

import java.util.*;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */
@Entity
@Tab(properties="name,mainBuilding.name") // name
public class Company extends Nameable {
		
	@OneToMany(mappedBy="company", cascade=CascadeType.REMOVE)
	@DetailAction("Company.saveBuildingFailing")
	@DetailAction("Company.failTrasactionInBuilding")
	private Collection<Building> buildings;
	
	@ReferenceView("Simple")
	@ManyToOne(fetch=FetchType.LAZY)
	private Building mainBuilding; // Must be here, below building collections, in order to test a bug

	public Collection<Building> getBuildings() {
		return buildings;
	}

	public void setBuildings(Collection<Building> buildings) {
		this.buildings = buildings;
	}
	
	public Building getMainBuilding() {
		return mainBuilding;
	}

	public void setMainBuilding(Building mainBuilding) {
		this.mainBuilding = mainBuilding;
	}

}

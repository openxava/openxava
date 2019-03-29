package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@View(name="Simple", members="number, name")
@Tab(name="OrderByName", properties="name", defaultOrder="${name}")
public class Driver {
	
	@Id @Column(length=5) @Required 	
	private int number;
	
	@Stereotype("NO_FORMATING_STRING") 
	@Column(length=40) @Required
	private String name;

	@Stereotype("NO_FORMATING_STRING") 
	@Column(length=2) @Required	
	private String type;
	
	// TYPE column is overlapped between drivingLicence and type 
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({ 
		@JoinColumn(name="DRIVINGLICENCE_LEVEL", referencedColumnName="LEVEL", insertable=false, updatable=false),  
		@JoinColumn(name="TYPE", referencedColumnName="TYPE", insertable=false, updatable=false) 
	})	
	private DrivingLicence drivingLicence;	
	private Integer drivingLicence_level; 	
	
	public DrivingLicence getDrivingLicence() {
		return drivingLicence;
	}

	public void setDrivingLicence(DrivingLicence licence) {
		// In this way because the column for type of driving lincence does not admit null
		this.drivingLicence = licence;
		this.drivingLicence_level = licence==null?null:licence.getLevel();		
		this.type = licence==null?null:licence.getType(); 			
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
		
}

package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class Computer extends Identifiable {
	
	@Required @Column(length=40)
	private String name;
	
	@Column(length=40)
	private String operatingSystem;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOperatingSystem() {
		return operatingSystem;
	}

	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}
	
}

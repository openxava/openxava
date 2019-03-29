package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza 
 */
@Entity
@Tab(properties="name", defaultOrder="${name} asc") 
public class Applicant extends Identifiable {
	
	@Column(length=40) @Required
	@Action("Applicant.showButtons") 
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Skill skill;
	
	@PrePersist
	private void prePersist() { 
		name = name + " CREATED";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Skill getSkill() {
		return skill;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
	}

}

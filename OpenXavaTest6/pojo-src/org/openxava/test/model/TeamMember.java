package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

@Entity
public class TeamMember extends Identifiable {
	
	@ManyToOne
	private Team team;
	
	@Column(length=30) @Required
	private String role;
	
	@ManyToOne @SearchAction("Team.searchPerson")
	private Human person;

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Human getPerson() {
		return person;
	}

	public void setPerson(Human person) {
		this.person = person;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

}

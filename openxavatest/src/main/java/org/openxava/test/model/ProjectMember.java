package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;


/**
 * 
 * @author Javier Paniza
 */

@Entity
@Tab(defaultOrder="${name}") 
@View(name="Simple") 
public class ProjectMember extends Nameable {
	
	@DescriptionsList(forViews="Simple") 
	@ManyToOne
	private Project project;
	
	@ManyToOne
	private ProjectTeam team; 
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		// Leave it in this way, just bare, to test a case
		this.project = project;
	}

	public ProjectTeam getTeam() {
		return team;
	}

	public void setTeam(ProjectTeam team) {
		this.team = team;
	}
	
}

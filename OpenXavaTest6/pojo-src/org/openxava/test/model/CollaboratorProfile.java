package org.openxava.test.model;

import java.io.*;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
@Tab(properties="collaborator.name, profile.description")
public class CollaboratorProfile implements Serializable {
	

	@Id	@ManyToOne
	private Collaborator collaborator; 	
	
	@Id 	
	@ManyToOne
	@DescriptionsList
	@JoinColumn(name="PROFILE_APPLICATION_CODE", referencedColumnName="APPLICATION_CODE") 
	@JoinColumn(name="PROFILE_CODE", referencedColumnName="CODE") 
	private Profile profile;

	public Collaborator getCollaborator() {
		return collaborator;
	}

	public void setCollaborator(Collaborator collaborator) {
		this.collaborator = collaborator;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	
}
package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class Training extends Identifiable {
		
	@Required @Column(length=40)
	private String description;
	
	@javax.validation.constraints.Size(min=1, max=3) 
	@ElementCollection
	private Collection<TrainingSession> sessions;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<TrainingSession> getSessions() {
		return sessions;
	}

	public void setSessions(Collection<TrainingSession> sessions) {
		this.sessions = sessions;
	}	

}

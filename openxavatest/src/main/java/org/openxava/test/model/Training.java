package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;
import org.openxava.test.actions.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@View(name="WithSections", members="sectionA {description} sectionB {sessions} sectionC {} ")
public class Training extends Identifiable {
		
	@Required @Column(length=40)
	@OnChange(forViews="WithSections", value=OnChangeYearAction.class)
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

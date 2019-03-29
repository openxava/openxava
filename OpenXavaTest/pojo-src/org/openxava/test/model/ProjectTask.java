package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza
 */
@Entity
public class ProjectTask extends Identifiable {
	
	@ManyToOne
	private Project project;
	
	@Required @Column(length=60)
	private String description;
	
	public enum Priority { LOW, MEDIUM, HIGH };
	private Priority priority;
	
	private Date dueDate;
	
	public static long count() { 
		Query query = XPersistence.getManager().createQuery("select count(*) from ProjectTask");
		return (Long) query.getSingleResult();
	}
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

}

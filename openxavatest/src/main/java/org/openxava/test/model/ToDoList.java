package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class ToDoList extends Nameable {
		
	@OneToMany(mappedBy="list", cascade=CascadeType.ALL)
	private Collection<ToDoTask> tasks;
	
	@OneToMany(mappedBy="list", cascade=CascadeType.ALL)
	private Collection<ToDoComponent> components;
	
	

	public Collection<ToDoComponent> getComponents() {
		return components;
	}

	public void setComponents(Collection<ToDoComponent> components) {
		this.components = components;
	}

	public Collection<ToDoTask> getTasks() {
		return tasks;
	}

	public void setTasks(Collection<ToDoTask> tasks) {
		this.tasks = tasks;
	} 

}

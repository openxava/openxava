package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class ToDoComponent extends Nameable {
	
	@ManyToOne
	private ToDoList list;
	
	@OneToMany(mappedBy="component")
	private Collection<ToDoComponentTask> componentsTasks;
	
	public ToDoList getList() {
		return list;
	}

	public void setList(ToDoList list) {
		this.list = list;
	}

	public Collection<ToDoComponentTask> getComponentsTasks() {
		return componentsTasks;
	}

	public void setComponentsTasks(Collection<ToDoComponentTask> componentsTasks) {
		this.componentsTasks = componentsTasks;
	}
	
}

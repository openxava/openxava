package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class ToDoTask extends Nameable {

	@ManyToOne
	private ToDoList list;

	@OneToMany(mappedBy="task", cascade=CascadeType.ALL)
	@ListProperties("component.name")
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

package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class ToDoComponentTask extends Identifiable {

	@ManyToOne
	@DescriptionsList
	private ToDoComponent component;
	
	@ManyToOne
	private ToDoTask task;
	
	public ToDoComponent getComponent() {
		return component;
	}

	public void setComponent(ToDoComponent component) {
		this.component = component;
	}

	public ToDoTask getTask() {
		return task;
	}

	public void setTask(ToDoTask task) {
		this.task = task;
	}
		
}

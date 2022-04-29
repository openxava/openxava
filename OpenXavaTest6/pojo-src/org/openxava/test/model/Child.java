package org.openxava.test.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;

import org.openxava.annotations.EntityValidator;
import org.openxava.annotations.PropertyValue;
import org.openxava.annotations.Tab;
import org.openxava.test.validators.ChildValidator;

@Entity
@IdClass(ChildId.class)
@EntityValidator(value = ChildValidator.class, properties = {
	@PropertyValue(name = "id"),
	@PropertyValue(name = "description")
})
@Tab(properties="parent.description, parent.id, id, description")
public class Child implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@ManyToOne(optional=true)
	private Parent parent; // To avoid using entity name
	
	@Id
	@Column(name = "CHILDID")
	private String id; 
	
	private String description;
	
	public Parent getParent() {
		return parent;
	}

	public void setParent(Parent parent) {
		this.parent = parent;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}

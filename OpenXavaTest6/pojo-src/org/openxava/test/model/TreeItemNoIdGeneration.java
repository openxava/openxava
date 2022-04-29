package org.openxava.test.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.openxava.annotations.View;

/**
 * 
 * @author Federico Alcantara 
 */
@Entity
@View(members="id;description")
public class TreeItemNoIdGeneration {
	@Id
	private Integer id;
	
	@Column(length=255)
	private String path;
	
	@Column(length=30)
	private String description;
	
	@ManyToOne
	private TreeContainerNoIdGeneration parentContainer;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TreeContainerNoIdGeneration getParentContainer() {
		return parentContainer;
	}

	public void setParentContainer(TreeContainerNoIdGeneration parentContainer) {
		this.parentContainer = parentContainer;
	}

	@Override
	public String toString() {
		return id.toString();
	}
}

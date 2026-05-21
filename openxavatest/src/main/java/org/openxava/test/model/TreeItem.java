package org.openxava.test.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import org.openxava.annotations.View;

/**
 * 
 * @author Federico Alcántara 
 */
@Entity
@View(members="description")
public class TreeItem {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length=255)
	private String path;
	
	@Column(length=30)
	private String description;
	
	@ManyToOne
	private TreeContainer parentContainer;
	
	private Integer treeOrder;

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

	public TreeContainer getParentContainer() {
		return parentContainer;
	}

	public void setParentContainer(TreeContainer parentContainer) {
		this.parentContainer = parentContainer;
	}

	public void setTreeOrder(Integer treeOrder) {
		this.treeOrder = treeOrder;
	}

	public Integer getTreeOrder() {
		return treeOrder;
	}
	
	@Override
	public String toString() {
		return id.toString();
	}
}

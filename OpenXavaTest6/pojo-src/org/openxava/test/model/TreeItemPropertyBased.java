package org.openxava.test.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.openxava.annotations.View;

/**
 * This is the same as TreeItem, except that
 * the annotations are on the property instead of the field member.
 * @author Federico Alcantara 
 */
@Entity
@Table(name="TREEITEM")
@View(members="description")
public class TreeItemPropertyBased {
	private Integer id;
	
	private String path;
	
	private String description;
	
	private TreeContainerPropertyBased parentContainer;
	
	private Integer treeOrder;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(length=255)
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Column(length=30)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToOne
	public TreeContainerPropertyBased getParentContainer() {
		return parentContainer;
	}

	public void setParentContainer(TreeContainerPropertyBased parentContainer) {
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

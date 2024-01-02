package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Federico Alcántara 
 */
@Entity
@View(members="description")
public class TreeItemTwo {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length=255)
	private String folder;
	
	@Column(length=30)
	private String description;
	
	@ManyToOne
	private TreeContainer parentContainer;
	
	private Integer treeOrder;
	
    public String getDescriptionItem() {
        return "D";
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
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

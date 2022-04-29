package org.openxava.test.model;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.openxava.annotations.Editor;
import org.openxava.annotations.ListProperties;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;

/**
 * 
 * @author Federico Alcántara 
 */

@Entity
@Views({
	@View(members="id; description; treeItems")
})
public class TreeContainerNoIdGeneration {
	@Id
	private Integer id;
	
	@Column(length=30)
	private String description;

	@OneToMany(mappedBy="parentContainer", cascade = CascadeType.REMOVE)
	@Editor("TreeView")
	@ListProperties("description")
	@OrderBy("id, path")
	private Collection<TreeItemNoIdGeneration> treeItems;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<TreeItemNoIdGeneration> getTreeItems() {
		return treeItems;
	}

	public void setTreeItems(Collection<TreeItemNoIdGeneration> treeItems) {
		this.treeItems = treeItems;
	}

}

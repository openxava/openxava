package org.openxava.test.model;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.openxava.annotations.Editor;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.ListProperties;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;

/**
 * 
 * @author Federico Alcántara 
 */

@Entity
@Views({
	@View(members="description; treeItems")
})
public class TreeContainerNoOrder {
	@Id @Hidden
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length=30)
	private String description;

	@OneToMany(mappedBy="parentContainer", cascade = CascadeType.REMOVE)
	@Editor("TreeView")
	@ListProperties("description")
	@OrderBy("id, path")
	private Collection<TreeItemNoOrder> treeItems;
	
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

	public Collection<TreeItemNoOrder> getTreeItems() {
		return treeItems;
	}

	public void setTreeItems(Collection<TreeItemNoOrder> treeItems) {
		this.treeItems = treeItems;
	}

}

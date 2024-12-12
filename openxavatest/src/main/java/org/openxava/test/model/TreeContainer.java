package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Federico Alcántara 
 */

@Entity
@Views({
	@View(members="description; treeItems; treeItemTwos; steps; treeItemNoIdGeneration"),
	@View(name="Simple", members="description; treeItems"),
	@View(name="Alternate", members="description;treeItems"),
	@View(name="NoDefaultPath", members="description; treeItemTwos")
})
public class TreeContainer {
	@Id @Hidden
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length=30)
	private String description;

	@OneToMany(mappedBy="parentContainer", cascade = CascadeType.REMOVE)
	@Editor(notForViews="Alternate", value = "TreeView")
	@Editor(forViews="Alternate", value = "TreeViewAlternate")
	@ListProperties("description")
	@OrderBy("path, treeOrder")
	private Collection<TreeItem> treeItems;
	
	@OneToMany(mappedBy="parentContainer", cascade = CascadeType.REMOVE)
	@Editor("TreeView")
	@ListProperties("description, descriptionItem")
	@OrderBy("folder, treeOrder")
	@Tree(pathProperty="folder", pathSeparator="-", idProperties="auxId") // auxId to test a id with a name other than 'id', for a bug
	private Collection<TreeItemTwo> treeItemTwos;
	
	@OneToMany(mappedBy="parentContainer", cascade = CascadeType.REMOVE)
	@ListProperties("description")
	@OrderBy("path, treeOrder")
	@Editor("TreeView")
	private Collection<Step> steps;
	
	@Editor(value="TreeView")
	@Tree(pathProperty = "way", idProperties = "code")
	@ListProperties("description")
	@OrderBy("way, theOrder")
	@OneToMany(mappedBy="parentContainer", cascade = CascadeType.REMOVE)
	private Collection<TreeItemNoIdGeneration> treeItemNoIdGeneration;

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

	public Collection<TreeItem> getTreeItems() {
		return treeItems;
	}

	public void setTreeItems(Collection<TreeItem> treeItems) {
		this.treeItems = treeItems;
	}

	public void setTreeItemTwos(Collection<TreeItemTwo> treeItemTwos) {
		this.treeItemTwos = treeItemTwos;
	}

	public Collection<TreeItemTwo> getTreeItemTwos() {
		return treeItemTwos;
	}
	
	public Collection<Step> getSteps() {
		return steps;
	}

	public void setSteps(Collection<Step> steps) {
		this.steps = steps;
	}

	public Collection<TreeItemNoIdGeneration> getTreeItemNoIdGeneration() {
		return treeItemNoIdGeneration;
	}

	public void setTreeItemNoIdGeneration(Collection<TreeItemNoIdGeneration> treeItemNoIdGeneration) {
		this.treeItemNoIdGeneration = treeItemNoIdGeneration;
	}
	
}

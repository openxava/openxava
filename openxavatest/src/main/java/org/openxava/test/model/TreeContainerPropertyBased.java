package org.openxava.test.model;

import java.util.Collection;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import org.openxava.annotations.Editor;
import org.openxava.annotations.Editors;
import org.openxava.annotations.Hidden;
import org.openxava.annotations.ListProperties;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;

/**
 * This is the same as the TreeContainer, except that
 * the annotations are on the property instead of the field member.
 * @author Federico Alcántara 
 */

@Entity
@Table(name="TREECONTAINER")
@Views({
	@View(name="Simple", members="description; treeItems")
})
public class TreeContainerPropertyBased {
	private Integer id;
	
	private String description;

	private Collection<TreeItemPropertyBased> treeItems;
	
	@Id @Hidden
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(length=30)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToMany(mappedBy="parentContainer", cascade = CascadeType.REMOVE)
	@Editors({
		@Editor(notForViews="Alternate", value = "TreeView"),
		@Editor(forViews="Alternate", value = "TreeViewAlternate")
	})
	@ListProperties("description")
	@OrderBy("path, treeOrder")
	public Collection<TreeItemPropertyBased> getTreeItems() {
		return treeItems;
	}

	public void setTreeItems(Collection<TreeItemPropertyBased> treeItems) {
		this.treeItems = treeItems;
	}

}

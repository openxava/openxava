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
	
	private Integer auxId; // to test idProperties
	
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
	
	public Integer getAuxId() {
		return auxId;
	}

	public void setAuxId(Integer auxId) {
		this.auxId = auxId;
	}
	
	@PostPersist
    public void postPersist() {
        this.auxId = this.id;
    }

	@Override
	public String toString() {
		return id.toString();
	}
}

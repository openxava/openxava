package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Federico Alcantara 
 */
@Entity
@View(members="id;code;way;description;theOrder;parentContainer")
public class TreeItemNoIdGeneration {
	@Id
	private Integer id;
	
	private int code;
	
	@Column(length=255)
	private String way;
	
	@Column(length=30)
	private String description;
	
	@Column(name="THE_ORDER")
	private int theOrder;
	
	@ManyToOne
	private TreeContainer parentContainer;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getWay() {
		return way;
	}

	public void setWay(String way) {
		this.way = way;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getTheOrder() {
		return theOrder;
	}

	public void setTheOrder(int theOrder) {
		this.theOrder = theOrder;
	}

	public TreeContainer getParentContainer() {
		return parentContainer;
	}

	public void setParentContainer(TreeContainer parentContainer) {
		this.parentContainer = parentContainer;
	}

	@Override
	public String toString() {
		return id.toString();
	}
}

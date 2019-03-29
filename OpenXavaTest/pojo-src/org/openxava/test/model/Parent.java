package org.openxava.test.model;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.openxava.annotations.Tab;

@Entity
@Tab(properties = "id, description")
public class Parent implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "PARENTID")
	private String id;
	private String description;
	@OneToMany(mappedBy="parent", fetch=FetchType.LAZY, cascade=CascadeType.REMOVE)
	private Collection<Child>children;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setChildren(Collection<Child> children) {
		this.children = children;
	}
	public Collection<Child> getChildren() {
		return children;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Parent other = (Parent) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}

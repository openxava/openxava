package org.openxava.test.model;

import org.openxava.model.*;

import java.util.*;

import javax.persistence.*;
import org.openxava.annotations.*;

@Entity
@View(members="value; children")
public class AsEmbedContainer extends Identifiable {
	
	@Column(length=40)
	private String value;
		
	@OneToMany(mappedBy = "container")
	@AsEmbedded
	private Collection<AsEmbed1> children;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Collection<AsEmbed1> getChildren() {
		return children;
	}

	public void setChildren(Collection<AsEmbed1> children) {
		this.children = children;
	}

}

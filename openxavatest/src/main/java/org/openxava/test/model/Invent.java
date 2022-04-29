package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class Invent extends Identifiable {
	
	@Column(length=40) @Required
	private String description;

	private int value;
	
	@OneToMany(mappedBy="invent", cascade=CascadeType.REMOVE)
	private Collection<InventDetail> details;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public Collection<InventDetail> getDetails() {
		return details;
	}

	public void setDetails(Collection<InventDetail> details) {
		this.details = details;
	}

}

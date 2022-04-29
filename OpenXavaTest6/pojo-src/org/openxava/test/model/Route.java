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
public class Route extends Identifiable {
	
	@Column(length=40) @Required
	private String description;
	
	@ElementCollection
	@ListProperties("description, km, customer.number, customer.name, product, carrier.number, carrier.name")
	private Collection<Visit> visits;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<Visit> getVisits() {
		return visits;
	}

	public void setVisits(Collection<Visit> visits) {
		this.visits = visits;
	}

}

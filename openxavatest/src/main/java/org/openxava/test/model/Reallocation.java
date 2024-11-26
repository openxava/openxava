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
@Views({
	@View(members="description; details"),
	@View(name="DetailsReadOnly", members="description; details"),
	@View(name="DetailsEditOnly", members="description; details"),
	@View(name="DetailsNoProduct", members="description; details"),
	@View(name="DetailsNoId", members="description; details; details2")
})
public class Reallocation extends Identifiable {
	
	@Column(length=40) @Required
	private String description;
	
	@ReadOnly(forViews="DetailsReadOnly") 
	@EditOnly(forViews="DetailsEditOnly") 
	@ElementCollection
	@ListProperties("place, product.number, product.description, product.unitPrice, done")
	@ListProperties(forViews="DetailsNoId", value="place, product.code, product.description, product.unitPrice, done")
	@ListProperties(forViews="DetailsNoProduct", value="place, done") 
	private Collection<ReallocationDetail> details;
	
	@ElementCollection
	@ListProperties(forViews="DetailsNoId", value="place, product.code, product.description, product.unitPrice, done")
	private Collection<ReallocationDetail2> details2;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<ReallocationDetail> getDetails() {
		return details;
	}

	public void setDetails(Collection<ReallocationDetail> details) {
		this.details = details;
	}

	public Collection<ReallocationDetail2> getDetails2() {
		return details2;
	}

	public void setDetails2(Collection<ReallocationDetail2> details2) {
		this.details2 = details2;
	}

	
}

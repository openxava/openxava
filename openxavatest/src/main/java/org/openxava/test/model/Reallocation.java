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
	@View(name="DetailsReadOnly"),
	@View(name="DetailsEditOnly"),
	@View(name="DetailsNoProduct") 
})
public class Reallocation extends Identifiable {
	
	@Column(length=40) @Required
	private String description;
	
	
	@ReadOnly(forViews="DetailsReadOnly") 
	@EditOnly(forViews="DetailsEditOnly") 
	@ElementCollection
	@ListProperties("place, product.number, product.description, product.unitPrice, done")
	@ListProperties(forViews="DetailsNoProduct", value="place, done") 
	private Collection<ReallocationDetail> details;

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

}

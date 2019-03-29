package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class WorkComplaint extends Identifiable {
	
	
	@DescriptionsList(descriptionProperties="year, number")
	@Required
	@ManyToOne(fetch=FetchType.LAZY)
	private WorkOrder order;
	
	
	@DescriptionsList
	@Required
	@ManyToOne(fetch=FetchType.LAZY)
	private WorkComplaintType type;
	 
	@Column(length=80) @Required
	private String description;

	public WorkOrder getOrder() {
		return order;
	}

	public void setOrder(WorkOrder order) {
		this.order = order;
	}

	public WorkComplaintType getType() {
		return type;
	}

	public void setType(WorkComplaintType type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


}

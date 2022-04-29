package org.openxava.test.model;

import org.openxava.model.*;
import java.util.*;
import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
public class WorkOrderRequisition extends Identifiable {

	@ManyToOne
	private WorkOrder workOrder;
	
	@Column(length=60)
	private String description;
	
	@OneToMany(mappedBy="workOrderRequisition",cascade = CascadeType.REMOVE)
	private Collection<WorkOrderRequisitionDetail> details;

	public WorkOrder getWorkOrder() {
		return workOrder;
	}

	public void setWorkOrder(WorkOrder workOrder) {
		this.workOrder = workOrder;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<WorkOrderRequisitionDetail> getDetails() {
		return details;
	}

	public void setDetails(Collection<WorkOrderRequisitionDetail> details) {
		this.details = details;
	}

		
}

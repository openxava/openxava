package org.openxava.test.model;

import org.openxava.model.*;

import javax.persistence.*;

/**
 * 
 * @author JavierPaniza 
 */

@Entity
public class WorkOrderRequisitionDetail extends Identifiable {

	@ManyToOne
	private WorkOrderRequisition workOrderRequisition;
	
	@Column(length=60)
	private String description;

	public WorkOrderRequisition getWorkOrderRequisition() {
		return workOrderRequisition;
	}

	public void setWorkOrderRequisition(WorkOrderRequisition workOrderRequisition) {
		this.workOrderRequisition = workOrderRequisition;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}

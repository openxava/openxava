package org.openxava.test.model;

import java.util.*;
import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
@IdClass(WorkOrderKey.class)
public class WorkOrder {
	
	@Id @Column(length=4)
	private Integer year;
	
	@Id @Column(length=6)
	private Integer number;
	
	@DescriptionsList
	@ManyToOne(fetch=FetchType.LAZY)
	private WorkOrderType type;  

	@OneToMany(cascade=CascadeType.REMOVE,mappedBy="workOrder")	
	private Collection<WorkOrderRequisition> requisitions;

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Collection<WorkOrderRequisition> getRequisitions() {
		return requisitions;
	}

	public void setRequisitions(Collection<WorkOrderRequisition> requisitions) {
		this.requisitions = requisitions;
	}

	public WorkOrderType getType() {
		return type;
	}

	public void setType(WorkOrderType type) {
		this.type = type;
	}

}

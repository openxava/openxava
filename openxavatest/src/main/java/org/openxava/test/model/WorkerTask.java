package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 *  
 * @author Javier Paniza
 */

@Entity @Getter @Setter
@Tab(properties="description", baseCondition = "${assignedTo.worker.nickName} = 'JUAN'")
public class WorkerTask {
	
	@Id @Column(length=5) @Required
	String id;

	@Column(length=40) @Required
	String description;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=true)
	@DescriptionsList(descriptionProperties="worker.nickName, period")
	WorkerPlan assignedTo;   	

}

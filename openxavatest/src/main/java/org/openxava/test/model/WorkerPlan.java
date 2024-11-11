package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;

/**
 * tmr 
 * @author Javier Paniza
 */

@Entity @Getter @Setter
public class WorkerPlan extends Identifiable {
	
	@DescriptionsList(descriptionProperties="nickName")
	@ManyToOne(optional=false)
	Worker worker;
	
	@Column(length = 20) @Required
	String period;
	
}

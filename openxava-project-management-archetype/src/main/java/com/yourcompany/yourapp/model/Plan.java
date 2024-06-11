package com.yourcompany.yourapp.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;

@Entity @Getter @Setter
@Tab(defaultOrder="${period.name} desc, ${worker.name} asc") 
public class Plan extends Identifiable {
	
	@DescriptionsList
	@ManyToOne(optional=false)
	Worker worker; 
	
	@DescriptionsList
	@ManyToOne(optional=false)
	Period period;
	
	@ListProperties("status.icon, title, type.icon, project.name, version.name") 
	@OneToMany(mappedBy="assignedTo")
	@OrderColumn(name="Plan_issues_ORDER")
	List<Issue> issues;

}

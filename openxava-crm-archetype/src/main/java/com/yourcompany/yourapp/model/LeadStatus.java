package com.yourcompany.yourapp.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;

@Entity @Getter @Setter
public class LeadStatus extends Identifiable {
	
	@Column(length=40) @Required
	String description;
	
	boolean finished;

}

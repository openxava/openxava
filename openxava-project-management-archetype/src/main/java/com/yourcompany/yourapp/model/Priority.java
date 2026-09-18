package com.yourcompany.yourapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import org.openxava.annotations.*;

import lombok.*;

@Entity @Getter @Setter
@Tab(defaultOrder="${level} desc")
public class Priority {
	
	@Id @Max(9)
	int level;
	
	@Column(length=40) @Required
	String description;

}

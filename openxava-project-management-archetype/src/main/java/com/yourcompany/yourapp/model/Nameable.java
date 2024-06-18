package com.yourcompany.yourapp.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;

@MappedSuperclass @Getter @Setter
public class Nameable extends Identifiable {

	@Column(length=40) @Required
	String name;
	
}

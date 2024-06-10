package com.yourcompany.yourapp.model;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

@MappedSuperclass @Getter @Setter
public class Iconable extends Nameable {

	@Column(length=40) @Stereotype("ICON")
	private String icon;
	
}

package com.yourcompany.@package@.model;

import java.math.*;
import java.time.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;
import lombok.*;

/**
 * This is an example of an entity.
 * 
 * Feel free feel to rename, modify or remove at your taste.
 */

@Entity @Getter @Setter
public class YourFirstEntity extends Identifiable {
	
	@Column(length=50) @Required
	String description;
	
	LocalDate date;
	
	BigDecimal amount;

}

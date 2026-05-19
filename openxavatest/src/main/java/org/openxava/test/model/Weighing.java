package org.openxava.test.model;

import java.time.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.Identifiable;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 * @since 7.7
 */

@Entity @Getter @Setter
public class Weighing extends Identifiable {
	
	@Required
	private LocalDate date;
	
	@Column(length=9) @Required
	private long grossWeight;
	
	@Column(length=9) @Required
	private long tare;
	
	@Column(length=50)
	private String description;
}

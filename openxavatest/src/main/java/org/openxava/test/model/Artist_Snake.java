package org.openxava.test.model;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;

/**
 * tmr
 * To test snake case works with OpenXava, though in Java is advisable
 * to use camel case instead.
 * 
 * @author Javier Paniza 
 */

@Entity
@Table(name="Artist")
@Tab(defaultOrder="${artist_name}")
@Getter @Setter
public class Artist_Snake extends Identifiable {
	
	@ManyToOne
	@JoinColumn(name="artiststudio_id")
	Studio artist_studio; 
	
	@Required
	@Column(name="name", length=255) @DisplaySize(70) 
	String artist_name;  
	
	@Max(90l)
	@Column(name="age")
	Integer artist_age;
	
	@DescriptionsList(descriptionProperties = "id, description")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="level_id")
	ActingLevel artist_level; 
		
}

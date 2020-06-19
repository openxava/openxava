package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
@Tab(properties="time, description, amountOfPeople, type.description")
public class Appointment extends Identifiable {
	
	@Stereotype("DATETIME") @Required
	private Date time;
	
	@Column(length=60) @Required
	private String description;
	
	private int amountOfPeople;
	
	@DescriptionsList
	@ManyToOne(fetch=FetchType.LAZY)
	private AppointmentType type; 

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getAmountOfPeople() {
		return amountOfPeople;
	}

	public void setAmountOfPeople(int amountOfPeople) {
		this.amountOfPeople = amountOfPeople;
	}

	public AppointmentType getType() {
		return type;
	}

	public void setType(AppointmentType type) {
		this.type = type;
	}
}

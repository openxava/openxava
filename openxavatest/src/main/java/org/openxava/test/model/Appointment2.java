package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
@Table(name="APPOINTMENT")
@Tab(properties="time, dateTime, description, peopleCount, type.description, extendedDescription",
	editableProperties="peopleCount, type.description, extendedDescription") 
@View(members="time; dateTime; description; peopleCount; type")
public class Appointment2 extends Identifiable {
	
	@Stereotype("DATETIME")
	@Required
	private Date time;
	
	@DateTime
	@Required
	private Date dateTime;
	
	@Column(length=60) @Required
	private String description;
	
	@Column(name="AMOUNTOFPEOPLE")
	@Max(13) 
	private int peopleCount;

	@org.hibernate.annotations.Formula("DESCRIPTION || ' ' || AMOUNTOFPEOPLE")
	private String extendedDescription; 
	
	@DescriptionsList
	@ManyToOne(fetch=FetchType.LAZY)
	private AppointmentType type; 

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AppointmentType getType() {
		return type;
	}

	public void setType(AppointmentType type) {
		this.type = type;
	}
	
	public String getExtendedDescription() {
		return extendedDescription;
	}

	public void setExtendedDescription(String extendedDescription) {
		this.extendedDescription = extendedDescription;
	}

	public int getPeopleCount() {
		return peopleCount;
	}

	public void setPeopleCount(int peopleCount) {
		this.peopleCount = peopleCount;
	}

}

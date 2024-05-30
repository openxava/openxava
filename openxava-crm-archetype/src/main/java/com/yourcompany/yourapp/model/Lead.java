package com.yourcompany.yourapp.model;

import java.time.*;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.annotations.Files;
import org.openxava.model.*;
import org.openxava.util.*;

import lombok.*;

@Entity @Getter @Setter
@View(members=
	"name, status, email, lastTouch;" + 
	"description { description }" +
	"remarks { remarks }" + 
	"activities { activities }" +
	"attachments { attachments }" 
)
@Tab(
	properties= "name, email, lastTouch, status.description, status.finished, description, remarks",	
	defaultOrder = "${status.description}"
) 
public class Lead extends Identifiable {
	
	@Column(length=40) @Required
	String name;
	
	@DescriptionsList
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	LeadStatus status;
	
	@Email
	@Column(length=80) @DisplaySize(40) 
	String email;
	
	@ReadOnly
	LocalDate lastTouch; 
	
	@HtmlText(simple = true) 
	// @Column(columnDefinition="MEDIUMTEXT") // MySQL
	@Column(columnDefinition="LONGVARCHAR") // HSQLDB
	String description;

	@HtmlText(simple = true) 
	// @Column(columnDefinition="MEDIUMTEXT") // MySQL
	@Column(columnDefinition="LONGVARCHAR") // HSQLDB
	String remarks;
	
	@ElementCollection @OrderBy("date")
	Collection<Activity> activities;
	
	@Files
	String attachments; 

	public void setActivities(Collection<Activity> activities) {
		this.activities = activities;
		Activity last = (Activity) XCollections.last(activities);
		if (last == null || last.getDate() == null) return;
		lastTouch = last.getDate();
	}

	public void setLastTouch(LocalDate lastTouch) {
	}
	
}

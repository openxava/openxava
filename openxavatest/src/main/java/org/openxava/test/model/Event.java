package org.openxava.test.model;

import java.sql.*;
import java.time.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;
import org.openxava.test.actions.*;
import org.openxava.test.filters.*;

import lombok.*;

@Entity
@Getter
@Setter
@Tab(name="DefaultName", 
filter=DefaultNameEnvFilter.class,
properties="startDate, endDate, createDate, name, description",
baseCondition="${name} = ?"
)
public class Event extends Identifiable{

	@OnChange(OnChangeVoidCalendarAction.class)
	LocalDate startDate;
	
	@OnChange(OnChangeVoidAction.class)
	LocalDate endDate;
	
	LocalTime endTime;
	
	@OnChange(OnChangeVoidAction.class)
	@Editor("DateTimeSeparatedCalendar")
	Timestamp createDate;
	
	String name;
	
	String description;
	
	@ManyToOne(fetch=FetchType.LAZY, optional = true)
	@DescriptionsList(condition="${paid} = false")
	Debt debtAtDate;
	
}

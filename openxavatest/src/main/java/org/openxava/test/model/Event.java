package org.openxava.test.model;

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
@View(members = " startDate; endDate; createDate; data {year, description}; extra {shortcut}")
@Tab(name="DefaultYearEnv", 
filter=DefaultYearEnvFilter.class,
properties="startDate, endDate, createDate, year, description",
baseCondition="${year} = ?"
)
public class Event extends Identifiable{

	@OnChange(OnChangeVoidCalendarAction.class)
	LocalDate startDate;
	
	@OnChange(OnChangeVoidAction.class)
	LocalDate endDate;
	
	@OnChange(OnChangeVoidAction.class)
	LocalDate createDate;
	
	int year;
	
	String description;
	
	String shortcut;
	
}

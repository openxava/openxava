package org.openxava.test.model;

import java.time.*;

import javax.persistence.*;

import org.openxava.model.*;

import lombok.*;

@Entity
@Getter
@Setter
public class Event extends Identifiable{

	LocalDate startDate;
	
	LocalDate endDate;
	
	LocalDate createDate;
	
	String description;
	
}

package com.yourcompany.yourapp.model;

import java.time.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;

import lombok.*;

@Embeddable @Getter @Setter
public class Activity {
		
	@Required
	@Column(length=120)
	String description;
	
	@Required
	@DefaultValueCalculator(CurrentLocalDateCalculator.class)
	LocalDate date;

}

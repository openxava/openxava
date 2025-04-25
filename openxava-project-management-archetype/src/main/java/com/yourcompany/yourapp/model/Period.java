package com.yourcompany.yourapp.model;

import java.time.*;

import javax.persistence.*;

import lombok.*;

@Entity @Getter @Setter
@Table(indexes = {
    @Index(name = "idx_start_date", columnList = "startDate"), 
    @Index(name = "idx_end_date", columnList = "endDate") 
})
public class Period extends Nameable {
	
	LocalDate startDate; 
	LocalDate endDate; 
	
}

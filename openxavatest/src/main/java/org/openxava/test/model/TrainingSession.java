package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;

/**
 * 
 * @author Javier Paniza
 */

@Embeddable
public class TrainingSession {

	@Column(length=30) @Required
	private String description;
	
	@javax.validation.constraints.Min(2) 
	@javax.validation.constraints.Max(50) 	
	private int kms;
	
	@DefaultValueCalculator(CurrentDateCalculator.class) 
	private Date date;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getKms() {
		return kms;
	}

	public void setKms(int kms) {
		this.kms = kms;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
}

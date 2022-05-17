package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@IdClass(CourseKey.class)
public class Course {
	
	@Id @Column(length=4)
	private int year;
	
	@Id @Column(length=5) @Hidden
	private int number;
	
	@Required
	private String description;
	
	@PrePersist
	private void calculateNumberValue() {
		number = (int) (System.currentTimeMillis() % 100000);
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}

package org.openxava.test.model;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */

public class QuarterKey implements java.io.Serializable {

	@ManyToOne
	private AcademicYear year;

	@Column(length = 1)
	private int quarter;


	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		return obj.toString().equals(this.toString());
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public String toString() {
		return "QuarterKey::" + year.getYear() + ":" + quarter;
	}
	
	public AcademicYear getYear() {
		return year;
	}

	public void setYear(AcademicYear year) {
		this.year = year;
	}

	public int getQuarter() {
		return quarter;
	}

	public void setQuarter(int quarter) {
		this.quarter = quarter;
	}
	

}

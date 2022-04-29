package org.openxava.test.model;

import javax.persistence.*;
import org.openxava.annotations.*;

@View(members="subfamily, year")
public class FilterBySubfamilyAndYear extends FilterBySubfamily {
	
	@Column(length=4)
	private int year;

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

}

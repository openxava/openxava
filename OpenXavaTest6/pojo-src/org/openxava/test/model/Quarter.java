package org.openxava.test.model;

import java.util.*;
import javax.persistence.*;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@IdClass(QuarterKey.class)
public class Quarter {

	@Id
	@ManyToOne
	@DescriptionsList
	private AcademicYear year;

	@Id
	@Column(length = 1)
	private int quarter;

	private Date initDate;

	private Date endDate;

	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "quarter")
	private Collection<QuarterSubject> subjects;
	
	
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

	public Date getInitDate() {
		return initDate;
	}

	public void setInitDate(Date initDate) {
		this.initDate = initDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Collection<QuarterSubject> getSubjects() {
		return subjects;
	}

	public void setSubjects(Collection<QuarterSubject> subjects) {
		this.subjects = subjects;
	}
	
}
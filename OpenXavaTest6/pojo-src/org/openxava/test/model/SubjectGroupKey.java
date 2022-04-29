package org.openxava.test.model;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */

public class SubjectGroupKey implements java.io.Serializable {

	@ManyToOne
	private QuarterSubject quarterSubject;

	@Column(length = 10)
	private String code;
	
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
		return "SubjectGroupKey::" + quarterSubject.getCode() + ":" + code;
	}
	
	public QuarterSubject getQuarterSubject() {
		return quarterSubject;
	}

	public void setQuarterSubject(QuarterSubject quarterSubject) {
		this.quarterSubject = quarterSubject;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@IdClass(SubjectGroupKey.class)
public class SubjectGroup {

	@Id
	@ManyToOne
	@DescriptionsList
	private QuarterSubject quarterSubject;

	@Id
	@Column(length = 10)
	private String code;

	@Column(length = 10)
	private String type;
	
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
package org.openxava.test.model;

import java.util.*;
import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@IdClass(QuarterSubjectKey.class)
public class QuarterSubject {

	@Id
	@ManyToOne
	private Quarter quarter;

	@Id @Column(length = 5)
	private String code;

	@Column(length = 50)
	private String description;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "quarterSubject")
	private Collection<SubjectGroup> groups;

	public Quarter getQuarter() {
		return quarter;
	}

	public void setQuarter(Quarter quarter) {
		this.quarter = quarter;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<SubjectGroup> getGroups() {
		return groups;
	}

	public void setGroups(Collection<SubjectGroup> groups) {
		this.groups = groups;
	}

}
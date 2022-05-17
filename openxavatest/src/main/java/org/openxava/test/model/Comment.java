package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class Comment {
	
	@ManyToOne 
	@Required 
	private Issue issue;
	
	@Id @Hidden
	@GeneratedValue(strategy=GenerationType.IDENTITY) 
	private Integer id;
	
	@Required @DefaultValueCalculator(CurrentDateCalculator.class)
	private java.util.Date date;
	
	@Stereotype("MEMO")
	private String comment;

	public Issue getIssue() {
		return issue;
	}

	public void setIssue(Issue issue) {
		this.issue = issue;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public java.util.Date getDate() {
		return date;
	}

	public void setDate(java.util.Date date) {
		this.date = date;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}

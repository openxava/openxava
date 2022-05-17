package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class BlogComment extends Identifiable {
	
	@ManyToOne
	private Blog parent;
		
	public Blog getParent() {
		return parent;
	}

	public void setParent(Blog parent) {
		this.parent = parent;
	}

	@Required @DefaultValueCalculator(CurrentDateCalculator.class)
	private java.util.Date date;
	
	@Stereotype("MEMO") @Column(length=500) @Required
	private String body;
		
	public java.util.Date getDate() {
		return date;
	}

	public void setDate(java.util.Date date) {
		this.date = date;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
}

package org.openxava.test.model;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.openxava.annotations.DescriptionsList;

/**
 * Create on 03/09/2009 (9:05:56)
 * @autor Ana Andres
 */
public class IssueList {
	
	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	private Issue issue;

	public Issue getIssue() {
		return issue;
	}

	public void setIssue(Issue issue) {
		this.issue = issue;
	}
	
}

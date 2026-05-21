package org.openxava.test.model;

import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;

import org.openxava.annotations.DescriptionsList;

/**
 * Create on 03/09/2009 (9:05:56)
 * @autor Ana Andrés
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

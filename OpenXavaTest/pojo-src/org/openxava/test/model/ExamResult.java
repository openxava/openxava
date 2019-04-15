package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class ExamResult extends Nameable {
	
	@ElementCollection
	private Collection<ExamQuestionResult> questionResults;

	public Collection<ExamQuestionResult> getQuestionResults() {
		return questionResults;
	}

	public void setQuestionResults(Collection<ExamQuestionResult> questionResults) {
		this.questionResults = questionResults;
	}

}

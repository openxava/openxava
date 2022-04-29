package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.test.actions.*;

/**
 *
 * @author Javier Paniza
 */

@Embeddable
public class ExamQuestionResult {
	
	@OnChange(OnChangeShowCollectionValuesAction.class)
	private boolean passed;

	public boolean isPassed() {
		return passed;
	}

	public void setPassed(boolean passed) {
		this.passed = passed;
	}

}

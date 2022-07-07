package org.openxava.test.model;

import javax.persistence.*;

/**
 * To test @javax.validation.constraints.Size with min and max elements on a 
 * collection that simulate embedding
 * 
 * @author Jeromy Altuna
 */
@Entity
/* tmr
@Audited
@AuditOverride(forClass=Nameable.class, isAudited=true)
*/
public class Question extends Nameable {
	
	@ManyToOne
	private Exam exam;

	public Exam getExam() {
		return exam;
	}

	public void setExam(Exam exam) {
		this.exam = exam;
	}	
}

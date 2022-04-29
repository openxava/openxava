package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */
@Entity
public class ServiceExpensesExplained extends ServiceExpenses {
	
	@Stereotype("MEMO")
	private String explanation;

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

}

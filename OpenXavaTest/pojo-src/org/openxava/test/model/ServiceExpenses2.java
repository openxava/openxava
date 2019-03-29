package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Jeromy Altuna
 */
@Entity
public class ServiceExpenses2 extends Identifiable {
	
	@Column(length=40)
	private String description;
	
	@ElementCollection
	@ListProperties("invoice.year, invoice.number, invoice.amount, status, receptionist") 
	private Collection<ServiceExpense2> expenses;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<ServiceExpense2> getExpenses() {
		return expenses;
	}

	public void setExpenses(Collection<ServiceExpense2> expenses) {
		this.expenses = expenses;
	}
}
